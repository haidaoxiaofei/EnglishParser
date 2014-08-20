/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsyncFileWriter implements MyFileWriter, Runnable, Printer {

    private final File file;
    private final Writer out;
    private final BlockingQueue<Item> queue = new LinkedBlockingQueue<Item>();
    private volatile boolean started = false;
    private volatile boolean stopped = false;

    public AsyncFileWriter(File file) throws IOException {
        this.file = file;
        this.out = new BufferedWriter(new FileWriter(file));
        this.open();
    }

    public MyFileWriter append(CharSequence seq) {
        if (!started) {
            throw new IllegalStateException("open() call expected before append()");
        }
        try {
            queue.put(new CharSeqItem(seq));
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    public MyFileWriter indent(int indent) {
        if (!started) {
            throw new IllegalStateException("open() call expected before append()");
        }
        try {
            queue.put(new IndentItem(indent));
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    public void open() {
        this.started = true;
        new Thread(this).start();
    }

    public void run() {
        while (!stopped) {
            try {
//                System.out.println("running...");
                Item item = queue.poll(500, TimeUnit.MICROSECONDS);
                if (item != null) {
                    try {
                        item.write(out);
                        
                    } catch (IOException logme) {
                    }
                }
            } catch (InterruptedException e) {
            }
        }
        try {
            out.close();
        } catch (IOException ignore) {
        }
    }

    public void close() {
        this.stopped = true;
    }

    @Override
    public void print(String content) {
        this.append(content);
    }

    private static interface Item {

        void write(Writer out) throws IOException;
    }

    private static class CharSeqItem implements Item {

        private final CharSequence sequence;

        public CharSeqItem(CharSequence sequence) {
            this.sequence = sequence;
        }

        public void write(Writer out) throws IOException {
            out.append(sequence + "\n");
        }
    }

    private static class IndentItem implements Item {

        private final int indent;

        public IndentItem(int indent) {
            this.indent = indent;
        }

        public void write(Writer out) throws IOException {
            for (int i = 0; i < indent; i++) {
                out.append(" ");
            }
        }
    }
}
