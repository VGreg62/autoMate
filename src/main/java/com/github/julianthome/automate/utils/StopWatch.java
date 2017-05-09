/**
 * autoMate - yet another automaton library for Java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Julian Thome <julian.thome.de@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package com.github.julianthome.automate.utils;

public class StopWatch {
    long start;
    long end;
    long time;
    long overallTime = 0;

    public static StopWatch get() {
        return new StopWatch();
    }

    private StopWatch() { start = 0; end = 0; time = 0; overallTime = 0;}

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long stop() {
        this.end = System.currentTimeMillis();

        if(this.end > this.start) {
            this.time = this.end - this.start;
        } else {
            this.time = 0L;
        }
        this.end = 0; this.start = 0;
        this.overallTime += time;
        return time;
    }

    public long getTime() {
        return this.time;
    }

    public long getOverallTime() {
        return this.overallTime;
    }

}