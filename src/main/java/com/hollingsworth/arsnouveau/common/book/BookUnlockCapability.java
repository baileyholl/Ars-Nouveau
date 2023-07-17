/*
 *
 *  * SPDX-FileCopyrightText: 2022 klikli-dev
 *  *
 *  * SPDX-License-Identifier: MIT
 *
 */

package com.hollingsworth.arsnouveau.common.book;

import com.hollingsworth.arsnouveau.common.book.conditions.BookEntryUnlockedCondition;
import com.hollingsworth.arsnouveau.common.book.conditions.context.BookConditionCategoryContext;
import com.hollingsworth.arsnouveau.common.book.conditions.context.BookConditionContext;
import com.hollingsworth.arsnouveau.common.book.conditions.context.BookConditionEntryContext;
import com.hollingsworth.arsnouveau.common.book.error.BookErrorManager;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BookUnlockCapability implements INBTSerializable<CompoundTag> {

    protected final Player player;

    /**
     * Map Book ID to read entry IDs
     */
    public ConcurrentMap<ResourceLocation, Set<ResourceLocation>> readEntries = new ConcurrentHashMap<>();

    /**
     * Map Book ID to unlocked entry IDs
     */
    public ConcurrentMap<ResourceLocation, Set<ResourceLocation>> unlockedEntries = new ConcurrentHashMap<>();

    /**
     * Map Book ID to unlocked categories IDs
     */
    public ConcurrentMap<ResourceLocation, Set<ResourceLocation>> unlockedCategories = new ConcurrentHashMap<>();

    /**
     * Map Book ID to commands used. This is never wiped to avoid reusing reward commands.
     */
    public ConcurrentMap<ResourceLocation, Map<ResourceLocation, Integer>> usedCommands = new ConcurrentHashMap<>();

    public BookUnlockCapability(Player player) {
        this.player = player;
    }

    public static String getUnlockCodeFor(Player player, Book book) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.getUnlockCode(book)).orElse("No unlocked content.");
    }

    public static Book applyUnlockCodeFor(ServerPlayer player, String unlockCode) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> {
            var book = c.applyUnlockCode(unlockCode);
            if (book != null) {
                c.sync(player);

            }
            return book;
        }).orElse(null);
    }

    public static void syncFor(ServerPlayer player) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(c -> c.sync(player));
    }

    public static void updateAndSyncFor(ServerPlayer player) {
        if (BookDataManager.get().areBooksBuilt()) {
            player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
                capability.update(player);
                capability.sync(player);
            });
        } else {
            //we have some edge cases where RecipesUpdatedEvent is fired after EntityJoinLevelEvent.
            //in SP this means that books are not built yet when updateAndSyncFor is called for the first time.
            //so we poll until it is available.
            var timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    player.server.execute(() -> {
                        updateAndSyncFor(player);
                    });
                }
            }, 1000);

        }
    }

    public static List<ResourceLocation> getBooksFor(Player player) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.getBooks()).orElse(Collections.emptyList());
    }

    public static void resetFor(Player player, Book book) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(c -> c.reset(book));
    }

    public static boolean isUnlockedFor(Player player, BookCategory category) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isUnlocked(category)).orElse(false);
    }

    public static boolean isUnlockedFor(Player player, BookEntry entry) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isUnlocked(entry)).orElse(false);
    }

    public static boolean isReadFor(Player player, BookEntry entry) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.isRead(entry)).orElse(false);
    }

    public static boolean canRunFor(Player player, BookCommand command) {
        return player.getCapability(CapabilityRegistry.BOOK_UNLOCK).map(c -> c.canRun(command)).orElse(false);
    }

    public static void setRunFor(Player player, BookCommand command) {
        player.getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(c -> c.setRun(command));
    }

    public static void onAdvancement(final AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverplayer) {
            updateAndSyncFor(serverplayer);
        }
    }

    /**
     * Clones the data from an existing instance
     *
     * @param other the existing instance.
     */
    public void clone(BookUnlockCapability other) {
        this.unlockedEntries = other.unlockedEntries;
        this.unlockedCategories = other.unlockedCategories;
        this.readEntries = other.readEntries;
        this.usedCommands = other.usedCommands;
    }

    /**
     * Always call sync afterwards!
     */
    public void update(ServerPlayer owner) {

        //loop through available books and update unlocked pages and categories
        List<Entry<BookEntryUnlockedCondition, BookConditionContext>> unlockedConditions = new ArrayList<>();

        for (var book : BookDataManager.get().getBooks().values()) {
            BookErrorManager.get().reset();
            BookErrorManager.get().setCurrentBookId(book.getId());
            for (var category : book.getCategories().values()) {
                BookErrorManager.get().setContext("Category to perform condition test on: {}",
                        category.getId().toString()
                );
                try {
                    var categoryContext = BookConditionContext.of(book, category);
                    if (category.getCondition().test(categoryContext, owner))
                        this.unlockedCategories.computeIfAbsent(book.getId(), k -> new HashSet<>()).add(category.getId());
                    else if (category.getCondition() instanceof BookEntryUnlockedCondition bookEntryUnlockedCondition)
                        unlockedConditions.add(Map.entry(bookEntryUnlockedCondition, categoryContext));
                } catch (Exception e) {
                    BookErrorManager.get().error("Error while testing category condition", e);
                }


                for (var entry : category.getEntries().values()) {
                    BookErrorManager.get().setContext("Entry to perform condition test on: {}",
                            entry.getId().toString()
                    );

                    try {
                        var entryContext = BookConditionContext.of(book, entry);
                        if (entry.getCondition().test(entryContext, owner))
                            this.unlockedEntries.computeIfAbsent(book.getId(), k -> new HashSet<>()).add(entry.getId());
                        else if (entry.getCondition() instanceof BookEntryUnlockedCondition bookEntryUnlockedCondition)
                            unlockedConditions.add(Map.entry(bookEntryUnlockedCondition, entryContext));
                    } catch (Exception e) {
                        BookErrorManager.get().error("Error while testing entry condition", e);
                    }
                }
            }
        }

        BookErrorManager.get().reset();

        boolean unlockedAny = false;
        do {
            unlockedAny = false;
            var iter = unlockedConditions.iterator();
            while (iter.hasNext()) {
                var condition = iter.next();
                BookErrorManager.get().setCurrentBookId(condition.getValue().getBook().getId());
                BookErrorManager.get().setContext("Context to perform unlockedConditions test on: {}",
                        condition.getValue().toString()
                );

                //check if condition is now unlocked
                if (condition.getKey().test(condition.getValue(), owner)) {
                    try {
                        //then store the unlock result
                        if (condition.getValue() instanceof BookConditionEntryContext entryContext) {
                            this.unlockedEntries.computeIfAbsent(entryContext.getBook().getId(), k -> new HashSet<>()).add(entryContext.getEntry().getId());
                        } else if (condition.getValue() instanceof BookConditionCategoryContext categoryContext) {
                            this.unlockedCategories.computeIfAbsent(categoryContext.getBook().getId(), k -> new HashSet<>()).add(categoryContext.getCategory().getId());
                        }

                        //make sure to iterate again now -> could unlock further conditions depending on this unlock
                        unlockedAny = true;

                        //remove the condition from the list, so it is not checked again
                        iter.remove();

                    } catch (Exception e) {
                        BookErrorManager.get().error("Error while testing entry condition", e);
                    }
                }
            }

            //now repeat until we no longer unlock anything
        } while (unlockedAny);

        BookErrorManager.get().reset();
    }

    public void sync(ServerPlayer player) {
        Networking.sendToPlayerClient(new SyncBookUnlockCapabilityMessage(this), player);
    }

    /**
     * @return true if entry is now read, false if it was already read before.
     */
    public boolean read(BookEntry entry) {
        if (this.isRead(entry))
            return false;

        this.readEntries.computeIfAbsent(entry.getBook().getId(), k -> new HashSet<>()).add(entry.getId());

        var command = entry.getCommandToRunOnFirstRead();
        if(command != null && this.player instanceof ServerPlayer serverPlayer) {
            command.execute(serverPlayer);
        }

        return true;
    }

    public void setRun(BookCommand command) {
        if (command.getBook() == null)
            return;

        var uses = this.usedCommands.getOrDefault(command.getBook().getId(), new HashMap<>()).getOrDefault(command.getId(), 0);
        this.usedCommands.computeIfAbsent(command.getBook().getId(), k -> new HashMap<>()).put(command.getId(), uses + 1);
    }

    public boolean canRun(BookCommand command) {
        if (command.getBook() == null)
            return false;

        if (command.getMaxUses() == -1) //unlimited uses
            return true;

        return this.usedCommands.getOrDefault(command.getBook().getId(), new HashMap<>()).getOrDefault(command.getId(), 0) < command.getMaxUses();
    }

    public boolean isRead(BookEntry entry) {
        if (entry.getBook() == null)
            return false;
        return this.readEntries.getOrDefault(entry.getBook().getId(), new HashSet<>()).contains(entry.getId());
    }

    public boolean isUnlocked(BookEntry entry) {
        if (entry.getBook() == null)
            return false;
        return this.unlockedEntries.getOrDefault(entry.getBook().getId(), new HashSet<>()).contains(entry.getId());
    }

    public boolean isUnlocked(BookCategory category) {
        if (category.getBook() == null)
            return false;
        return this.unlockedCategories.getOrDefault(category.getBook().getId(), new HashSet<>()).contains(category.getId());
    }

    public void reset(Book book) {
        this.readEntries.remove(book.getId());
        this.unlockedEntries.remove(book.getId());
        this.unlockedCategories.remove(book.getId());
        //Do not reset the commands!
    }

    public List<ResourceLocation> getBooks() {
        var books = new HashSet<ResourceLocation>();
        books.addAll(this.readEntries.keySet());
        books.addAll(this.unlockedEntries.keySet());
        books.addAll(this.unlockedCategories.keySet());
        return books.stream().toList();
    }

    public String getUnlockCode(Book book) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(book.getId());

        var unlockedCategories = this.unlockedCategories.getOrDefault(book.getId(), Set.of());
        buf.writeVarInt(unlockedCategories.size());
        unlockedCategories.forEach(buf::writeResourceLocation);

        var unlockedEntries = this.unlockedEntries.getOrDefault(book.getId(), Set.of());
        buf.writeVarInt(unlockedEntries.size());
        unlockedEntries.forEach(buf::writeResourceLocation);

        var readEntries = this.readEntries.getOrDefault(book.getId(), Set.of());
        buf.writeVarInt(readEntries.size());
        readEntries.forEach(buf::writeResourceLocation);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes);
    }

    public Book applyUnlockCode(String code) {
        try {
            var decoded = Base64.getDecoder().decode(code);
            var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(decoded));
            var bookId = buf.readResourceLocation();

            var book = BookDataManager.get().getBook(bookId);
            if (book == null)
                return null;

            var unlockedCategories = new HashSet<ResourceLocation>();
            var unlockedEntries = new HashSet<ResourceLocation>();
            var readEntries = new HashSet<ResourceLocation>();
            var unlockedCategoriesSize = buf.readVarInt();
            for (var i = 0; i < unlockedCategoriesSize; i++) {
                unlockedCategories.add(buf.readResourceLocation());
            }

            var unlockedEntriesSize = buf.readVarInt();
            for (var i = 0; i < unlockedEntriesSize; i++) {
                unlockedEntries.add(buf.readResourceLocation());
            }

            var readEntriesSize = buf.readVarInt();
            for (var i = 0; i < readEntriesSize; i++) {
                readEntries.add(buf.readResourceLocation());
            }

            this.unlockedCategories.put(bookId, unlockedCategories);
            this.unlockedEntries.put(bookId, unlockedEntries);
            this.readEntries.put(bookId, readEntries);

            return book;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var compound = new CompoundTag();

        compound.putString(ModonomiconConstants.Nbt.VERSION_TAG, ModonomiconConstants.Nbt.CURRENT_VERSION);

        var unlockedCategoriesByBook = new ListTag();
        compound.put("unlocked_categories", unlockedCategoriesByBook);
        this.unlockedCategories.forEach((bookId, categories) -> {
            var bookCompound = new CompoundTag();
            var unlockedCategoriesList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("unlocked_categories", unlockedCategoriesList);

            categories.forEach(categoryId -> {
                var categoryCompound = new CompoundTag();
                categoryCompound.putString("category_id", categoryId.toString());
                unlockedCategoriesList.add(categoryCompound);
            });

            unlockedCategoriesByBook.add(bookCompound);
        });

        var unlockedEntriesByBook = new ListTag();
        compound.put("unlocked_entries", unlockedEntriesByBook);
        this.unlockedEntries.forEach((bookId, entries) -> {
            var bookCompound = new CompoundTag();
            var unlockedEntriesList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("unlocked_entries", unlockedEntriesList);

            entries.forEach(entryId -> {
                var entryCompound = new CompoundTag();
                entryCompound.putString("entry_id", entryId.toString());
                unlockedEntriesList.add(entryCompound);
            });

            unlockedEntriesByBook.add(bookCompound);
        });

        var readEntriesByBook = new ListTag();
        compound.put("read_entries", readEntriesByBook);
        this.readEntries.forEach((bookId, entries) -> {
            var bookCompound = new CompoundTag();
            var readEntriesList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("read_entries", readEntriesList);

            entries.forEach(entryId -> {
                var entryCompound = new CompoundTag();
                entryCompound.putString("entry_id", entryId.toString());
                readEntriesList.add(entryCompound);
            });

            readEntriesByBook.add(bookCompound);
        });

        var usedCommandsByBook = new ListTag();
        compound.put("used_commands", usedCommandsByBook);
        this.usedCommands.forEach((bookId, commands) -> {
            var bookCompound = new CompoundTag();
            var usedCommandsList = new ListTag();
            bookCompound.putString("book_id", bookId.toString());
            bookCompound.put("used_commands", usedCommandsList);

            commands.forEach((command, used) -> {
                var commandCompound = new CompoundTag();
                commandCompound.putString("command", command.toString());
                commandCompound.putInt("used", used);
                usedCommandsList.add(commandCompound);
            });

            usedCommandsByBook.add(bookCompound);
        });

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.unlockedCategories.clear();

        var unlockedCategoriesByBook = nbt.getList("unlocked_categories", Tag.TAG_COMPOUND);
        for (var bookEntry : unlockedCategoriesByBook) {
            if (bookEntry instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var unlockedCategoriesList = bookCompound.getList("unlocked_categories", Tag.TAG_COMPOUND);
                var categories = new HashSet<ResourceLocation>();
                for (var categoryEntry : unlockedCategoriesList) {
                    if (categoryEntry instanceof CompoundTag categoryCompound) {
                        var categoryId = ResourceLocation.tryParse(categoryCompound.getString("category_id"));
                        categories.add(categoryId);
                    }
                }
                this.unlockedCategories.put(bookId, categories);
            }
        }

        this.unlockedEntries.clear();
        var unlockedEntriesByBook = nbt.getList("unlocked_entries", Tag.TAG_COMPOUND);
        for (var bookEntry : unlockedEntriesByBook) {
            if (bookEntry instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var unlockedEntriesList = bookCompound.getList("unlocked_entries", Tag.TAG_COMPOUND);
                var entries = new HashSet<ResourceLocation>();
                for (var entry : unlockedEntriesList) {
                    if (entry instanceof CompoundTag pageCompound) {
                        var entryId = ResourceLocation.tryParse(pageCompound.getString("entry_id"));
                        entries.add(entryId);
                    }
                }
                this.unlockedEntries.put(bookId, entries);
            }
        }

        this.readEntries.clear();
        var readEntriesByBook = nbt.getList("read_entries", Tag.TAG_COMPOUND);
        for (var bookEntry : readEntriesByBook) {
            if (bookEntry instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var readEntriesList = bookCompound.getList("read_entries", Tag.TAG_COMPOUND);
                var entries = new HashSet<ResourceLocation>();
                for (var entry : readEntriesList) {
                    if (entry instanceof CompoundTag pageCompound) {
                        var entryId = ResourceLocation.tryParse(pageCompound.getString("entry_id"));
                        entries.add(entryId);
                    }
                }
                this.readEntries.put(bookId, entries);
            }
        }

        this.usedCommands.clear();
        var usedCommandsByBook = nbt.getList("used_commands", Tag.TAG_COMPOUND);
        for (var usedCommand : usedCommandsByBook) {
            if (usedCommand instanceof CompoundTag bookCompound) {
                var bookId = ResourceLocation.tryParse(bookCompound.getString("book_id"));
                var usedCommandsList = bookCompound.getList("used_commands", Tag.TAG_COMPOUND);
                var commands = new HashMap<ResourceLocation, Integer>();
                for (var command : usedCommandsList) {
                    if (command instanceof CompoundTag commandCompound) {
                        var commandId = ResourceLocation.tryParse(commandCompound.getString("command"));
                        var used = commandCompound.getInt("used");
                        commands.put(commandId, used);
                    }
                }
                this.usedCommands.put(bookId, commands);
            }
        }
    }

    public static class Dispatcher implements ICapabilitySerializable<CompoundTag> {

        private final BookUnlockCapability bookUnlockCapability;
        private final LazyOptional<BookUnlockCapability> bookUnlockCapabilityLazy;

        public Dispatcher(Player player) {
            this.bookUnlockCapability = new BookUnlockCapability(player);
            this.bookUnlockCapabilityLazy = LazyOptional.of(() -> this.bookUnlockCapability);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityRegistry.BOOK_UNLOCK) {
                return this.bookUnlockCapabilityLazy.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.bookUnlockCapability.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.bookUnlockCapability.deserializeNBT(nbt);
        }
    }
}
