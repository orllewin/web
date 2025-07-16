# Distance between two points

```
val dx = this.x - other.x
val dy = this.y - other.y
val distance = sqrt(dx * dx + dy * dy)
```

embedHtml:point_distance_html.html

```
const originX = 150
const originY = 150

function loop() {
	clear()

	circle(originX, originY, 3)
	circle(mouseX, mouseY, 3)
	line(originX, originY, mouseX, mouseY)
	
	let dx = originX - mouseX
	let dy = originY - mouseY
	let distance = floor(sqrt(dx * dx + dy * dy))
	text("" + distance, 5, 20, 16)
	redraw()
}
```

Made with [Coracle](../../coracle.js)