/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class SyncBookDataMessage {

    public ConcurrentMap<ResourceLocation, Book> books = new ConcurrentHashMap<>();

    public SyncBookDataMessage(ConcurrentMap<ResourceLocation, Book> books) {
        this.books = books;
    }

    public SyncBookDataMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.books.size());
        for (var book : this.books.values()) {
            buf.writeResourceLocation(book.getId());
            book.toNetwork(buf);

            buf.writeVarInt(book.getCategories().size());
            for (var category : book.getCategories().values()) {
                buf.writeResourceLocation(category.getId());
                category.toNetwork(buf);

                buf.writeVarInt(category.getEntries().size());
                for (var entry : category.getEntries().values()) {
                    buf.writeResourceLocation(entry.getId());
                    entry.toNetwork(buf);
                }
            }

            buf.writeVarInt(book.getCommands().size());
            for (var command : book.getCommands().values()) {
                buf.writeResourceLocation(command.getId());
                command.toNetwork(buf);
            }
        }
    }

    public void decode(FriendlyByteBuf buf) {
        //build books
        int bookCount = buf.readVarInt();
        for (int i = 0; i < bookCount; i++) {
            ResourceLocation bookId = buf.readResourceLocation();
            Book book = Book.fromNetwork(bookId, buf);
            this.books.put(bookId, book);

            int categoryCount = buf.readVarInt();
            for (int j = 0; j < categoryCount; j++) {
                ResourceLocation categoryId = buf.readResourceLocation();
                BookCategory category = BookCategory.fromNetwork(categoryId, buf);

                //link category and book
                book.addCategory(category);

                int entryCount = buf.readVarInt();
                for (int k = 0; k < entryCount; k++) {
                    ResourceLocation entryId = buf.readResourceLocation();
                    BookEntry bookEntry = BookEntry.fromNetwork(entryId, buf);

                    //link entry and category
                    category.addEntry(bookEntry);
                }
            }

            int commandCount = buf.readVarInt();
            for (int j = 0; j < commandCount; j++) {
                ResourceLocation commandId = buf.readResourceLocation();
                BookCommand command = BookCommand.fromNetwork(commandId, buf);

                //link command and book
                book.addCommand(command);
            }
        }
    }

    public void handle(Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BookDataManager.get().onDatapackSyncPacket(this);
        });
        ctx.get().setPacketHandled(true);
    }
}
