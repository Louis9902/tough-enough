package io.github.louis9902.toughenough.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public final class NBTCollectors {

    public static <T> Collector<T, ?, CompoundTag> toCompound(Function<? super T, String> keys, Function<? super T, ? extends Tag> values) {
        return Collector.of(CompoundTag::new, accumulator(keys, values), merger());
    }

    private static <T> BiConsumer<CompoundTag, T> accumulator(Function<? super T, String> keys, Function<? super T, ? extends Tag> values) {
        return (compound, element) -> {
            String k = keys.apply(element);
            Tag v = Objects.requireNonNull(values.apply(element));
            if (compound.contains(k)) {
                throw new IllegalStateException();
            }
            compound.put(k, v);
        };
    }

    private static BinaryOperator<CompoundTag> merger() {
        return (a, b) -> {
            for (String key : b.getKeys()) {
                if (a.contains(key)) {
                    throw new IllegalStateException();
                }
                a.put(key, Objects.requireNonNull(b.get(key)));
            }
            return a;
        };
    }

}
