Chapter 2 Challenge: The Applet Archaeologist

1. Security: Based on the "sandbox" model, list two things you think an applet would NOT be allowed to do on your computer.
- Access local files or directories outside the applet's JAR (no reading/writing user docs without permission).
- Connect to arbitrary network hosts (only origin server allowed, no p2p or external APIs).

2. Modern Replacement: What combination of three modern web technologies (e.g., HTML, CSS, JavaScript) has completely replaced the need for Java Applets? Give a brief example of how you might create a simple bouncing text animation using these technologies.
HTML5 Canvas (for drawing), CSS (for styling/animation timing), JavaScript (for logic/movement). Example: 
<html><canvas id="c" width=400 height=200 style="border:1px solid;"></canvas><script>
let canvas=document.getElementById('c'), ctx=canvas.getContext('2d'), x=10;
setInterval(()=>{ctx.clearRect(0,0,400,200); ctx.fillText('Your Name',x,50); x+=5; if(x>300)x=0;},100);
</script></html>
(Script loops every 100ms: clear, draw text at x, increment/reset x.)

3. Legacy: Why is it still important for a developer to know what applets are, even if they are deprecated?
Understanding applets teaches sandbox security, threading in GUIs, and lifecycle methods—principles in modern apps (e.g., Android Activities, web workers). Plus, legacy codebases exist; knowing history avoids reinventing wheels.

AppletViewer Test: Ran applet.html—text bounces smoothly across gray background. Deprecated warnings noted, but functional.