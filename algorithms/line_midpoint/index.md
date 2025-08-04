# Line midpoint

embedHtml:line_midpoint_html.html

```
const originX = 150
const originY = 150

function loop() {
	clear()

	circle(originX, originY, 3)
	circle(mouseX, mouseY, 3)
	line(originX, originY, mouseX, mouseY)
	
	let lineLength = distance(originX, originY, mouseX, mouseY)
	let a = angle(originX, originY, mouseX, mouseY)
	let cX = originX + (cos(a) * lineLength/2)
	let cY = originY + (sin(a) * lineLength/2)
	circle(cX, cY, 3)
	
	text(`${floor(cX)}x${floor(cY)}`, 5, 20, 16)
	redraw()
}
```

Made with [Coracle](../../coracle.js)