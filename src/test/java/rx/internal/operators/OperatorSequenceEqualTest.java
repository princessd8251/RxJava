/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.internal.operators;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.InOrder;

import rx.*;
import rx.exceptions.TestException;
import rx.functions.Func2;

public class OperatorSequenceEqualTest {
    @Test
    public void constructorShouldBePrivate() {
        TestUtil.checkUtilityClass(OperatorSequenceEqual.class);
    }

    @Test
    public void test1() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just("one", "two", "three"),
                Observable.just("one", "two", "three"));
        verifyResult(observable, true);
    }

    @Test
    public void test2() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just("one", "two", "three"),
                Observable.just("one", "two", "three", "four"));
        verifyResult(observable, false);
    }

    @Test
    public void test3() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just("one", "two", "three", "four"),
                Observable.just("one", "two", "three"));
        verifyResult(observable, false);
    }

    @Test
    public void testWithError1() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.concat(Observable.just("one"),
                        Observable.<String> error(new TestException())),
                Observable.just("one", "two", "three"));
        verifyError(observable);
    }

    @Test
    public void testWithError2() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just("one", "two", "three"),
                Observable.concat(Observable.just("one"),
                        Observable.<String> error(new TestException())));
        verifyError(observable);
    }

    @Test
    public void testWithError3() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.concat(Observable.just("one"),
                        Observable.<String> error(new TestException())),
                Observable.concat(Observable.just("one"),
                        Observable.<String> error(new TestException())));
        verifyError(observable);
    }

    @Test
    public void testWithEmpty1() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.<String> empty(),
                Observable.just("one", "two", "three"));
        verifyResult(observable, false);
    }

    @Test
    public void testWithEmpty2() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just("one", "two", "three"),
                Observable.<String> empty());
        verifyResult(observable, false);
    }

    @Test
    public void testWithEmpty3() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.<String> empty(), Observable.<String> empty());
        verifyResult(observable, true);
    }

    @Test
    public void testWithNull1() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just((String) null), Observable.just("one"));
        verifyResult(observable, false);
    }

    @Test
    public void testWithNull2() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just((String) null), Observable.just((String) null));
        verifyResult(observable, true);
    }

    @Test
    public void testWithEqualityError() {
        Observable<Boolean> observable = Observable.sequenceEqual(
                Observable.just("one"), Observable.just("one"),
                new Func2<String, String, Boolean>() {
                    @Override
                    public Boolean call(String t1, String t2) {
                        throw new TestException();
                    }
                });
        verifyError(observable);
    }

    private void verifyResult(Observable<Boolean> observable, boolean result) {
        @SuppressWarnings("unchecked")
        Observer<Boolean> observer = mock(Observer.class);
        observable.subscribe(observer);

        InOrder inOrder = inOrder(observer);
        inOrder.verify(observer, times(1)).onNext(result);
        inOrder.verify(observer).onCompleted();
        inOrder.verifyNoMoreInteractions();
    }

    private void verifyError(Observable<Boolean> observable) {
        @SuppressWarnings("unchecked")
        Observer<Boolean> observer = mock(Observer.class);
        observable.subscribe(observer);

        InOrder inOrder = inOrder(observer);
        inOrder.verify(observer, times(1)).onError(isA(TestException.class));
        inOrder.verifyNoMoreInteractions();
    }
}
