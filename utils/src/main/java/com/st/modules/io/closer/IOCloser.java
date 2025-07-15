package com.st.modules.io.closer;


import com.st.modules.io.closer.exception.IOCloseException;
import com.st.modules.io.closer.exception.IOFlushException;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.function.Consumer;

public final class IOCloser {

    private IOCloser() {}

    public static void close(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            throw new IOCloseException("关闭资源失败: " + closeable.getClass().getName(), e);
        }
    }

    public static void closeAll(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable c : closeables) {
            close(c);
        }
    }

    public static void closeReader(java.io.Reader reader) {
        close(reader);
    }

    public static void closeWriter(java.io.Writer writer) {
        close(writer);
    }

    public static void closeInputStream(java.io.InputStream inputStream) {
        close(inputStream);
    }

    public static void closeOutputStream(java.io.OutputStream outputStream) {
        close(outputStream);
    }

    public static void closeInputStreamReader(java.io.InputStreamReader reader) {
        close(reader);
    }

    public static void closeOutputStreamWriter(java.io.OutputStreamWriter writer) {
        close(writer);
    }


    public static void flush(Flushable flushable) {
        if (flushable == null) return;
        try {
            flushable.flush();
        } catch (IOException e) {
            throw new IOFlushException("刷新失败: " + flushable.getClass().getName(), e);
        }
    }

    public static void flushAll(Flushable... flushables) {
        if (flushables == null) return;
        for (Flushable f : flushables) {
            flush(f);
        }
    }

    public static <T extends Closeable> void use(T resource, Consumer<T> consumer) {
        try (T res = resource) {
            consumer.accept(res);
        } catch (IOException e) {
            throw new IOCloseException("资源处理失败: " + resource.getClass().getName(), e);
        }
    }
}

