# richtextview [ ![Download](https://api.bintray.com/packages/zung435/richtextview/richtextview/images/download.svg?version=1.1) ](https://bintray.com/zung435/richtextview/richtextview/1.1/link) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/zung/richtextview)
Render rich text for android. 
Android Api level 18+
The supported HTML tags are div p br span a i u big img...
# Getting Started
- maven
```
<dependency>
	<groupId>com.czg.richtextview</groupId>
	<artifactId>richtextview</artifactId>
	<version>1.1</version>
	<type>pom</type>
</dependency>
```

- gradle
```
implementation 'com.czg.richtextview:richtextview:1.1'
```

- ivy
```
<dependency org="com.czg.richtextview" name="richtextview" rev="1.1">
	<artifact name="richtextview" ext="pom"></artifact>
</dependency>
```
# Sample code
```
TextView  textView = findViewById(R.id.text);
textView.setMovementMethod(LinkMovementMethod.getInstance());
String src = "<div id=\"se-knowledge\"><p><big>H</big>ello <i>world!</i></p>"
	+ "<img src=\"https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF\"/>"
	+ "<ul>" +
	"<li><a href=\"http://aa\">1.java</a></li>" +
	"<li><a>2.c++</a></li>" +
	"<li><a>3.python</a></li>" +
	"<li><a href=\"http://aa\">4.kotlin</a></li>" +
	"</ul>"
	+ "successful!"
	+ "</div>";
MyHtml.init(textView);
Spanned spanned = MyHtml.fromHtml(src, MyHtml.FROM_HTML_MODE_COMPACT, new MyHtml.ImageGetter() {
    @Override
    public Drawable getDrawable(String source, int start) {
	getImage(source, start);
	return null;
    }
}, null);
textView.setText(spanned);
```
Last, you need to call the method MyHtml.onImageReady(bitmap, start) to show the bitmap obtained by any method.
# Screen shoot
![blockchain](https://github.com/zung/richtextview/blob/main/app/result.png?raw=true "Screen shoot")
