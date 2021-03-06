import gab.opencv.*;
import processing.video.*;
import static javax.swing.JOptionPane.*;
import oscP5.*;
import netP5.*;

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

int bttnBoxColor = #1d60fe;
int textBttnColor = color(225);
int textSize = (28);
int backgroundC = #b4b4b4;

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

void settings() {
    
    size(sW, sH);

}

void setup() {

    font = createFont("Arial", 32);
    textFont(font);

    oscP5 = new OscP5(this, 59867);

    uielement = new UIElements();

}

void draw() {
    
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
void drawMainMenu() {

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

void drawUI() {

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
void notificationHandler() {
    
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
void oscEvent(OscMessage msg) {

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