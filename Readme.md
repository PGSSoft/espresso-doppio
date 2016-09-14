##Espresso doppio
Tools for Espresso based UI tests

This library was designed to make implementing end-to-end test suites easier. It approaches synchronization problem in way that allows keep main source set unpolluted from test code - synchronization is based on visible components. Provided IdlingResources can wait for particular View, Fragment or Activity, that matches provided Matcher.

### Usage ###

As usual - add it to your build.gradle:
```
dependencies {
    androidTestCompile 'com.pgs-soft:espressodoppio:1.0.0'
}
```

#### Idling resources
Every Espresso doppio IdlingResource extends BaseIdlingResource. This class provides default implementation for timeout configuration, idle state transition and shortcuts for registration/unregistration. **Provided IdlingResource implementations are designed in way that does not require any additional implementation in main source set for testing synchronization purposes.** Espresso doppio way of testing is based on things that are visible for user - views (or views containers like Activities or Fragments),  there is no need to monitor requests in background if such request result will provide visual change.

#####IdlingResource that waits for view:
```java
final ViewIdlingResource viewIdlingResource = new ViewIdlingResource(
    both(withId(R.id.item_recycler)).and(DoppioMatchers.recyclerWithItems)
).register();
//here trigger Espresso ViewAction that will wait until above one will be idle, for example:
onView(withId(R.id.item_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
viewIdlingResource.unregister();
```
Same code can be called with helper method that will done registration/unregistration automatically:
```java
final ViewIdlingResource viewIdlingResource = new ViewIdlingResource(
    both(withId(R.id.item_recycler)).and(DoppioMatchers.recyclerWithItems) //any Matcher<View> can be used
);

waitForIdle(viewIdlingResource, new Runnable() {
    @Override
    public void run() {
        onView(withId(R.id.item_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
});
```

#####IdlingResource that waits for Activity:
```java
//Waits for MainActivity to be RESUMED
final ActivityIdlingResource activityIdlingResource = new ActivityIdlingResource(MainActivity.class);
//always remember about unregister!
```

#####IdlingResource that waits for Fragment to be added
```java
//Waits for Fragment with provided tag
final FragmentIdlingResource res2 = new FragmentIdlingResource(YesNoDialog.FRAGMENT_TAG).register();
//Waits for Fragment with provided id
final FragmentIdlingResource res2 = new FragmentIdlingResource(R.id.fragment_smt).register();
//Waits for Fragment that matches provided matchers
final FragmentIdlingResource res = new FragmentIdlingResource(
        allOf(
                DoppioMatchers.withId(R.id.fragment_list_container),
                instanceOf(DetailFragment.class))
        ).register();
```

#####IdlingResource that waits until timeout, should be used as last resort. Such timeout can make your tests flaky!
```java
final TimeoutIdlingResource timeout = new TimeoutIdlingResource(6000).register();
```

#### ComponentFinder
Utility class based on InstrumentationRegistry that allows access current Activity, search for views or fragments. Used internally by Espresso doppio IdlingResources.
#### Manual ViewAssertions
Is approach for Espresso ViewAssertion implementation that will always pass for matched view, but instead allows to extract value from View. For example text from TextView. It's handy when UI tests have to be wrapped in some abstract container like PageObject but assertion has to be done on TestSuite level. Sample usage:
```java
String text = DoppioExtractors.extractText(withId(R.id.dialog_start_button));
Assert.assertEquals(text, "Start dialog");
```
extractText(Matcher<View>) internally uses extended ViewManualAssertion that is check on matching View. Such ViewAssertions for data extracting can be implemented by extending ViewManualAssertion class.
#### DoppioMatchers
Custom matchers for View/Fragment/Activity matching.

### License ###
MIT License

Copyright (c) 2016 PGS Software SA

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
