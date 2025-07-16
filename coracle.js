/**
    A little-by-little port of Coracle to oldschool Javascript
    Archived Kotlin Coracle: https://github.com/orllewin/coracle
 */

var canvas = document.getElementById("canvas");
var context = canvas.getContext("2d");

const width = canvas.width
const height = canvas.height

let xTranslate = 0
let yTranslate = 0

const TWO_PI = 6.2831855

var fps = null

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
function random(max){
    return Math.floor(Math.random() * max)
}

function sin(n){
    return Math.sin(n)
}

function cos(n){
    return Math.cos(n)
}