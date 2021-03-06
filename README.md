[![](https://jitpack.io/v/Omega-R/OmegaHtmlTagHandler.svg)](https://jitpack.io/#Omega-R/OmegaHtmlTagHandler)
# OmegaHtmlTagHandler

OmegaHtmlTagHandler is handler for html tag, which can load and converts it into Spannable for displaying it.
It is a addition for usage of the fromHtml, which can not process all tags
OmegaHtmlTagHandler allows you control over how tags are rendered. Allows proper handling of tables (support tag: table, tr, th, td)

# HTML
![examplefrom](https://user-images.githubusercontent.com/14843818/28017778-91bccdac-652f-11e7-918c-18863a5059fb.png)

# Textview
![exampleto](https://user-images.githubusercontent.com/14843818/28017856-d1b3a5c0-652f-11e7-8411-9239714e0072.png)

# Installation
To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
**Step 2.** Add the dependency
```
dependencies {
    compile 'com.github.Omega-R:OmegaHtmlTagHandler:v1.0'
}
```

# Usage
Example of usage
```
OmegaHtmlTagHandler handler = new OmegaHtmlTagHandler();
Spanned spanned;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
  spanned = Html.fromHtml(mHtmlTable, Html.FROM_HTML_MODE_LEGACY, null, handler);
} else {
  spanned = Html.fromHtml(mHtmlTable, null, handler);
}
textViewHtmlTable.setText(spanned);
```
You also can delete specific spaces in table if needs, and can set type space:
```
handler.deleteSpecificSpaces(true, "\\\\n");
```
And can set specifies what character will be between the rows of the table:
```
handler.setSymbolsBetweenTableRows("\n");
```
