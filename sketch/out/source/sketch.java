import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch extends PApplet {



Capture cam;
int c;

public void setup() {
    
    cam = new Capture(this);
    cam.start();
    c = (0);
}

public void draw() {
    if (cam.available() == true) {
        cam.read();
    }


    image(cam, 0,0);

    fill(c);
    rect(0, 0, 100, 100);

}

public void mousePressed() {
    loadPixels();
    println(pixels[mouseY*width+mouseX]);
    c = pixels[mouseY*width+mouseX];
}
  public void settings() {  size(600, 300); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
