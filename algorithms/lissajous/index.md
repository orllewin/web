# Lissajous

[Lissajous curve](https://en.wikipedia.org/wiki/Lissajous_curve)

embedHtml:lissajous_html.html

```
const scale = 110.0
var freqA = 0.0
var freqB = 0.0
var prevX = -1.0
var prevY = -1.0
var x = 0.0
var y = 0.0
var angle = 0.0

function initialise() {
	clear()
	
	frame = 0
	
	translate(width/2, height/2)
	freqA = randomFloat(5.0, 100.0)
	freqB = randomFloat(5.0, 100.0)
	
	angle = (frame / freqB * TWO_PI)
	prevX = cos(angle) * scale
	
	angle = (frame / freqA * TWO_PI)
	prevY = sin(angle) * scale
	
	frame++
	
	stroke("#000000")
	fill("#ffffff01")
}

function loop() {
	if(frame > 1000) initialise()
	
	angle = (frame / freqB * TWO_PI)
	x = cos(angle) * scale
	
	angle = (frame / freqA * TWO_PI)
	y = sin(angle) * scale
	
	line(prevX, prevY, x, y)
	
	prevX = x
	prevY = y
	
	//overdraw
	rect(0, 0, width, height)
	
	redraw()
}
```