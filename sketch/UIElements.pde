class UIElements {

    int returnBttnW = 130;
    int returnBttnH = 50;
    int returnBttnX = 540 + returnBttnW;
    int returnBttnY = sH - returnBttnH;

    //Constructor
    UIElements() {

    }

    //Sender brugeren tilbage til start-menuen, når "Tilbage" bliver trykket på.
    void returnBttn() {

        if(button(returnBttnX, returnBttnX+returnBttnW, returnBttnY, returnBttnY+returnBttnH, 200, 0, textSize, "Tilbage")) {
            currentScene = 0;
        }

    }
    
    //Knap-funktion, hvor der ikke bliver tegnet en boks, men kun registrere museklik indenfor de angivede parametre.
    boolean button(int minX, int maxX, int minY, int maxY) {
        if (mouseX > minX && mouseX < maxX && mouseY > minY && mouseY < maxY && mousePressed == true) {
            mousePressed = false;
            return true;
        } else {
            return false;
        }
    }

    //Knap-funktion, der tegner og registrere museklik indenfor de angivede parametre.
    boolean button(int minX, int maxX, int minY, int maxY, int rectColor, int textColor, int bTextSize, String textString) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

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
    boolean button(int minX, int maxX, int minY, int maxY, int rectColor, int textColor, int bTextSize, String textString, int rectMode) { //Funkktion der opfører sig som en knap, ved at registrere om musen bliver klikket indenfor en given region.

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
    int optionDialog(String paneName, String paneMessage, Object opt1, Object opt2, Object opt3) {
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
    void informationDialog(String paneName, String paneMessage, String messageType) {
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