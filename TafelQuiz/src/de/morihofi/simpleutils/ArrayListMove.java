package de.morihofi.simpleutils;


import java.util.List;
import java.util.Locale;


public class ArrayListMove {
    public static <E> void move(List<E> list, int fromIndex, int toIndex) {
        if (fromIndex >= list.size() || fromIndex < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(fromIndex, list.size()));
        if (toIndex >= list.size() || toIndex < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(toIndex, list.size()));
        if (fromIndex == toIndex) return;
        int from = fromIndex;
        E item;
        if (fromIndex < toIndex) {
            item = list.set(from, list.get(from + 1));
            from++;
            while (from < toIndex) {
                list.set(from, list.get(from + 1));
                from++;
            }
        } else {
            item = list.set(from, list.get(from - 1));
            from--;
            while (from > toIndex) {
                list.set(from, list.get(from - 1));
                from--;
            }
        }
        list.set(toIndex, item);
    }

    private static String outOfBoundsMsg(int index, int size) {
        return String.format(Locale.getDefault(), "Index: %d, Size: %d", index, size);
    }


}
