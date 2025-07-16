/**
    A little-by-little port of Coracle to oldschool Javascript
    Archived Kotlin Coracle: https://github.com/orllewin/coracle
 */

var canvas = document.getElementById("canvas")
var context = canvas.getContext("2d")

canvas.style.cursor = "none"
canvas.style.border = "thin solid #000000"

const canvasRect = canvas.getBoundingClientRect()
const width = canvas.width
const height = canvas.height

var mouseX = 0
var mouseY = 0

let xTranslate = 0
let yTranslate = 0

const TWO_PI = 6.2831855

var fps = null

document.addEventListener("mousemove", mouseMoveHandler, false)

function mouseMoveHandler(e) {
    mouseX = event.clientX - canvasRect.left
    mouseY = event.clientY - canvasRect.top
}

window.requestAnimationFrame(loop)

function redraw(){
    if(fps == null){
        window.requestAnimationFrame(loop)
    }else{
        setTimeout(() => {
            requestAnimationFrame(loop)
        }, 1000 / fps)
    }
}

function clear() {
    context.clearRect(0, 0, width, height)
    context.beginPath()
}

function background(color) {
    context.beginPath()
    context.fillStyle = color
    context.fillRect(0, 0, width, height)
}

function fill(colour){
    context.fillStyle = colour
}

function translate(x, y){
    xTranslate = x
    yTranslate = y
}

//Drawing

function text(text, x, y, size){
    context.font = "" + size + "px sans"
    context.fillText(text, x, y)
}

function line(x1, y1, x2, y2) {
    context.moveTo(xTranslate + x1, yTranslate + y1)
    context.lineTo(xTranslate + x2, yTranslate + y2)
    context.stroke()
}

function circle(x, y, r){
    context.beginPath()
    context.arc(xTranslate + x, yTranslate + y, r, 0, 2 * Math.PI)
    context.fill()
}

//Math

function floor(n){
    return Math.floor(n)
}

function random(max){
    return Math.floor(Math.random() * max)
}

function sin(n){
    return Math.sin(n)
}

function cos(n){
    return Math.cos(n)
}

function sqrt(n){
    return Math.sqrt(n)
}