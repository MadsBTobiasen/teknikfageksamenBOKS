import processing.video.*;

Capture cam;
color c;

void setup() {
    size(600, 300);
    cam = new Capture(this);
    cam.start();
    c = (0);
}

void draw() {
    if (cam.available() == true) {
        cam.read();
    }


    image(cam, 0,0);

    fill(c);
    rect(0, 0, 100, 100);

}

void mousePressed() {
    loadPixels();
    println(pixels[mouseY*width+mouseX]);
    c = pixels[mouseY*width+mouseX];
}