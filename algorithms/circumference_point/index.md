# Circumference point

Given a line from the origin of a circle get the point it intersects the circumference.

```
//Pseudocode
val line = Line(this.x, this.y, other.x, other.y)
val angle = line.angle()
val cX = this.x + (cos(angle) * radius)
val cY = this.y + (sin(angle) * radius)
```

embedHtml:circumference_point_html.html

```
const originX = 150
const originY = 150
const circleRadius = 75

function loop() {
	clear()

	fill("#eeeeee")
	circle(originX, originY, circleRadius)
	
	fill("#000000")
	circle(originX, originY, 3)
	circle(mouseX, mouseY, 3)
	line(originX, originY, mouseX, mouseY)
	
	let a = angle(originX, originY, mouseX, mouseY)
	let cX = originX + (cos(a) * circleRadius)
	let cY = originY + (sin(a) * circleRadius)
	circle(cX, cY, 3)
	
	text(`${floor(cX)}x${floor(cY)}`, 5, 20, 16)
	redraw()
}
```

Made with [Coracle](../../coracle.js)