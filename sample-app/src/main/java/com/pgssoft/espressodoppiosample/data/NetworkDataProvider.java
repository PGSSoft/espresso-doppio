/*
 * MIT License
 *
 * Copyright (c) 2016 PGS Software SA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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
 */
package com.pgssoft.espressodoppiosample.data;


import com.pgssoft.espressodoppiosample.models.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func0;


public class NetworkDataProvider implements DataProvider {

    private List<ListItem> items = null;

    private List<ListItem> generateDummyItems() {
        List<ListItem> dummyItems = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            ListItem item = new ListItem("Item " + i);
            dummyItems.add(item);
        }

        return dummyItems;
    }

    @Override
    public Observable<List<ListItem>> getAllItems(List<ListItem> items) {
        //dumb cache emulation

        if (items != null && !items.isEmpty()) {
            return Observable.empty(); //already got newest data
        }

        return Observable.defer(new Func0<Observable<List<ListItem>>>() {
            @Override
            public Observable<List<ListItem>> call() {

                if (NetworkDataProvider.this.items != null) {
                    return Observable.just(NetworkDataProvider.this.items);
                }
                NetworkDataProvider.this.items = generateDummyItems();
                final Random rand = new Random();
                return Observable.just(NetworkDataProvider.this.items).delay(rand.nextInt(10) + 1, TimeUnit.SECONDS);
            }
        });
    }
}
