import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gab.opencv.*; 
import processing.video.*; 
import static javax.swing.JOptionPane.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch extends PApplet {







PFont font;
OscP5 oscP5;
UIElements uielement;

String localAddress = NetInfo.lan();
boolean pillFound = false;
int pillFoundC = color(0, 0, 0);

//Variabler til GUI.
int sW = 800;
int sH = 600;
int currentScene = 0;

int bttnBoxColor = 0xff1d60fe;
int textBttnColor = color(225);
int textSize = (28);
int backgroundC = 0xffb4b4b4;

int titlePixelsFromEdgeX = 125;
int titlePixelsFromEdgeY = 35;
int titleH = 6*titlePixelsFromEdgeY;
int titleW = sW - 2*titlePixelsFromEdgeX;
int titleX = titlePixelsFromEdgeX;
int titleY = titlePixelsFromEdgeY;

int optionsW = 200;
int optionsH = 50;
int optionsSeperationFromTitle = 100;
int optionsSeperation = 15;
int optionsX = sW/2;
int optionsY = titleY+titleH+optionsSeperationFromTitle;
String[] optionsStrings = {"Pille Status", "Notifikation om: ...", "Din IP: "};

boolean infoBoxOnStartup = true;
boolean infoBoxInStart = true;

String rTime = "0";
int tTime = second();
int timeBetweenNotificationSeconds = 30;
boolean readyForNotification = false;
boolean setBool = false;

//Slut.

public void settings() {
    
    size(sW, sH);

}

public void setup() {

    font = createFont("Arial", 32);
    textFont(font);

    oscP5 = new OscP5(this, 59867);

    uielement = new UIElements();

}

public void draw() {
    
    background(backgroundC);

    if (currentScene==0) {

        drawMainMenu();
        if (infoBoxOnStartup) {
            uielement.informationDialog("Velkommen", "Velkommen til EPBox til patienter.\n\nFor at benytte EPBox til patienter, skal du blot trykke på 'Start'", "information");
            infoBoxOnStartup = false;
        }

    }

    if (currentScene==1) {

        drawUI();
        if (infoBoxInStart) {
            uielement.informationDialog("Information", "For at benytte EPBox skal du sørge for at gennemføre opsætning i EPBox til Ordinering\nHvis du har brug for hjælp til opsætning, så følg guiden til opsætning der medfølger i brugsanvisningen.", "information");
            infoBoxInStart = false;
        }

    }

}

//Funktioner til UI.
//Tegner en hovedmenu, som brugeren lander på, når programmet åbnes.
public void drawMainMenu() {

        stroke(0);
        textSize(titleH/4);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
 
        noFill();
        rect(titleX, titleY, titleW, titleH);
        fill(0);
        text("EPBox: Patient", titleX, titleY, titleW, titleH-15);

        int xformedX = optionsX - optionsW/2;
        int xformedY = optionsY - optionsH/2;

        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*0, xformedY+optionsH+(optionsSeperation+optionsH)*0, bttnBoxColor, textBttnColor, textSize, "Start", RADIUS)) currentScene=1;
        if (uielement.button(xformedX, xformedX+optionsW, xformedY+(optionsSeperation+optionsH)*1, xformedY+optionsH+(optionsSeperation+optionsH)*1, bttnBoxColor, textBttnColor, textSize, "Luk EPBox", RADIUS)) exit();
        
}

public void drawUI() {

        stroke(0);
        textSize(titleH/4);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
 
        noFill();
        rect(titleX, titleY, titleW, titleH);
        fill(0);
        text("EPBox: Patient", titleX, titleY, titleW, titleH-15);

        textSize(textSize);
        //Tegn menu-elementer, og deres relevante tekster.
        for (int i = 0; i < 3; ++i) {
            rectMode(RADIUS);            

            //If statement til at kontrollere farven, til pille-status boksen.
            if(pillFound) pillFoundC = color(255, 0, 0);
            else pillFoundC = color(0, 255, 0);

            //Gør noget specielt baseret på hvilken boks der tegnes.
            textAlign(LEFT, CENTER);
            switch(i) {
                case 0:

                    fill(bttnBoxColor);
                    rect(optionsX, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);
                    
                    fill(textBttnColor);
                    text(optionsStrings[i], optionsX+10, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);

                    fill(pillFoundC);
                    rect(optionsX+optionsW-optionsW/4, optionsY+(optionsH+optionsSeperation)*i, optionsW/4, optionsH/2);

                    break;

                case 1:

                    fill(bttnBoxColor);
                    rect(optionsX, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);
                    
                    fill(textBttnColor);
                    text(optionsStrings[i], optionsX+10, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);   

                    textAlign(RIGHT, CENTER);
                    text(rTime, optionsX-10, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);
                    
                    break;
                
                case 2:

                    fill(bttnBoxColor);
                    rect(optionsX, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);

                    fill(textBttnColor);
                    text(optionsStrings[i], optionsX+10, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);

                    textAlign(RIGHT, CENTER);
                    text(localAddress, optionsX-10, optionsY+(optionsH+optionsSeperation)*i, optionsW, optionsH/2);
                    
                    break;

                default:
                    break;
            }
        
        }

        //Send notifikiation her.
        notificationHandler();
        //UI-knapper.
        uielement.returnBttn();
        //uielement.infoBttn();

}


//Funktion til at håndtere sending af notifikationer til brugeren.
public void notificationHandler() {
    
    tTime = second();

    if (tTime >= 31) {
        tTime = 30+(timeBetweenNotificationSeconds-tTime);
    } else if (tTime <= 29) {
        tTime = abs(tTime-timeBetweenNotificationSeconds);
    }

    rTime = str(tTime);

    //Kører kun If-statement, hvis timeBetween..Seconds, kan divideres med det nuværende sekund.
    //Har nogle kontrol-bools, således at brugeren kun for en notifikation hvert timeBetween...Seconds, istedet for en notifikation så længe det er dét sekundt.
    if ((second() % timeBetweenNotificationSeconds) == 0) {
        if(setBool) {
            readyForNotification = true;
            setBool = false;
        }
    } else {
        setBool = true;
    }

    //Sender notifikation
    if (readyForNotification && pillFound) {
        uielement.informationDialog("Pille blev fundet", "Du har ikke taget din pille!\nSkynd dig at finde din pilleæske, og tag din pille!", "warning");
        readyForNotification = false;
    }


}

//Funktion der bliver kaldet når programmet modtager en OSC-besked.
public void oscEvent(OscMessage msg) {

    println("Incomming OSC: " + msg.typetag() + " -- @address: "+ msg.addrPattern());

    switch(msg.typetag()) {
        case "F":
            pillFound = false;
            break;

        case "T":
            pillFound = true;
            break;

        default:
            println("invalid osc message");
            break;

    }

}
class UIElements {

    int returnBttnW = 130;
    int returnBttnH = 50;
    int returnBttnX = 540 + returnBttnW;
    int returnBttnY = sH - returnBttnH;

    //Constructor
    UIElements() {

    }

    //Sender brugeren tilbage til start-menuen, når "Tilbage" bliver trykket på.
    public void returnBttn() {

        if(button(returnBttnX, returnBttnX+returnBttnW, returnBttnY, returnBttnY+returnBttnH, 200, 0, textSize, "Tilbage")) {
            currentScene = 0;
        }

    }
    
    //Knap-funktion, hvor der ikke bliver tegnet en boks, men kun registrere museklik indenfor de angivede parametre.
    public boolean button(int minX, int maxX, int minY, int maxY) {
        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }
    }

    //Knap-funktion, der tegner og registrere museklik indenfor de angivede parametre.
    public boolean button(int minX, int maxX, int minY, int maxY, int rectColor, int textColor, int bTextSize, String textString) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        textSize(bTextSize);
        textAlign(CENTER, CENTER);
        rectMode(CORNER);
        stroke(0);

        fill(rectColor);
        rect(minX, minY, dist(minX, 0, maxX, 0), dist(0, minY, 0, maxY));
        fill(textColor);
        text(textString, minX, minY, dist(minX, 0, maxX, 0), dist(0, minY, 0, maxY));

        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }

    }

    //Knap-funktion, der tegner og registrere museklik indenfor de angivede parametre. Tagers også en rectMode, hvis der er brug for det.
    public boolean button(int minX, int maxX, int minY, int maxY, int rectColor, int textColor, int bTextSize, String textString, int rectMode) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

        boolean output = false;

        textSize(bTextSize);
        textAlign(CENTER, CENTER);
        stroke(0);

        if (rectMode == RADIUS) {

            rectMode(rectMode);

            fill(rectColor);
            rect(minX+dist(minX, 0, maxX, 0)/2, minY+dist(minY, 0, maxY, 0)/2, dist(minX, 0, maxX, 0), dist(minY, 0, maxY, 0)/2);
            fill(textColor);
            text(textString, minX+dist(minX, 0, maxX, 0)/2, minY+dist(minY, 0, maxY, 0)/2, dist(minX, 0, maxX, 0), dist(minY, 0, maxY, 0)/2);

            if (mouseX > minX-dist(minX, 0, maxX, 0)/2 && mouseX < maxX*2 && mouseY > minY && mouseY < maxY && mousePressed == true) {
                mousePressed = false;
                output = true;
            }
        
        }

        return output;

    }    

    //Mulighed boks der returnere en værdi, tilsvarende af den valgte mulighed af brugeren.
    public int optionDialog(String paneName, String paneMessage, Object opt1, Object opt2, Object opt3) {
        Object[] options = {opt1, opt2, opt3};

        //showOptionDialog laver en dialog box der viser 3 muligheder, disse 3 muligheder er blevet angivet i options-array'et.
        //showOptionDialog retunere en værdi mellem 0-2, alt efter hvilken mulighed der blev valgt
        return showOptionDialog(
            frame, paneMessage, paneName,
            YES_NO_CANCEL_OPTION,
            QUESTION_MESSAGE,
            null, options, options[2]
        );

    }

    //Information boks med kun en besked.
    /*void informationDialog(String paneMessage) {
        showMessageDialog(frame, paneMessage);
    }*/

    //Information boks, med navn, besked og beskedtype.
    public void informationDialog(String paneName, String paneMessage, String messageType) {
        int[] messageTypes = {PLAIN_MESSAGE, INFORMATION_MESSAGE, ERROR_MESSAGE, WARNING_MESSAGE};
        int arrayEntry = 0;

        //Switch case til at håndtere hvilket dialog-type der skal vises.
        switch(messageType.toLowerCase()) {
            case "plain":
                arrayEntry = 0;
                break;

            case "information":
                arrayEntry = 1;
                break;

            case "error":
                arrayEntry = 2;
                break;

            case "warning":
                arrayEntry = 3;
                break;     

            default:
                arrayEntry = 0;
                break;                           
        }
        
        showMessageDialog(
            frame, paneMessage, paneName,
            messageTypes[arrayEntry]
        );

    }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
