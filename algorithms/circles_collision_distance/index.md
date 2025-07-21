# Circles collision distance

The distance between the nearest point of two circles

```
//Pseudocode
val dx = this.x - other.x
val dy = this.y - other.y
val distance = sqrt(dx * dx + dy * dy) - this.radius - other.radius
```

embedHtml:circles_collision_distance_html.html

```
const circleAX = 0
const circleAY = 300
const circleARadius = 150

const circleBX = 300
const circleBY = 0
const circleBRadius = 100

function loop() {
	noStroke()
	
	//Circle a
	fill("#eeeeee")
	circle(circleAX, circleAY, circleARadius)
	
	let aA = angle(circleAX, circleAY, circleBX, circleBY)
	let circAX = circleAX + (cos(aA) * circleARadius)
	let circAY = circleAY + (sin(aA) * circleARadius)
	
	fill("#000000")
	circle(circAX, circAY, 3)
	
	//Circle b
	fill("#eeeeee")
	circle(circleBX, circleBY, circleBRadius)
	
	let aB = angle(circleBX, circleBY, circleAX, circleAY)
	let circBX = circleBX + (cos(aB) * circleBRadius)
	let circBY = circleBY + (sin(aB) * circleBRadius)
	
	fill("#000000")
	circle(circBX, circBY, 3)
	
	stroke("#ff0000")
	line(circAX, circAY, circBX, circBY)
	
	//Distance calculation
	let dx = circleAX - circleBX
	let dy = circleAY - circleBY
	let distance = sqrt(dx * dx + dy * dy) - circleARadius - circleBRadius
	
	fill("#000000")
	text(`${floor(distance)}`, 165, 150, 12)
}
```

Made with [Coracle](../../coracle.js)