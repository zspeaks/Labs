import java.util.Arrays;
import java.util.Scanner;
public class RleProgram {

    public static void main(String[] args){
        // Variables that will be used for loop control or throughout the loop
        int userMenuInput = 1;
        Scanner scanner = new Scanner(System.in);
        byte[] byteArray = new byte[0];
        //Prints the menu
        System.out.println("Welcome to the RLE image encoder!");
        System.out.println();
        System.out.println("Displaying Spectrum Image:");
        ConsoleGfx.displayImage(ConsoleGfx.testRainbow);
        System.out.println();
        //Loop to get user input, call appropriate method, and then repeat
        while(userMenuInput != 0) {
            System.out.println("RLE Menu");
            System.out.println("--------");
            System.out.println("0. Exit");
            System.out.println("1. Load File");
            System.out.println("2. Load Test Image");
            System.out.println("3. Read RLE String");
            System.out.println("4. Read RLE Hex String");
            System.out.println("5. Read Data Hex String");
            System.out.println("6. Display Image");
            System.out.println("7. Display RLE String");
            System.out.println("8. Display Hex RLE Data");
            System.out.println("9. Display Hex Flat Data");
            System.out.println();
            System.out.print("Select a Menu Option: ");
            userMenuInput = scanner.nextInt();
            System.out.println();
            //Loads a file from a string based filename
            if(userMenuInput == 1){
                System.out.print("Enter name of file to load: ");
                String fileName = scanner.next();
                System.out.println();
                byteArray = ConsoleGfx.loadFile(fileName);
            }//Loads the test image
            else if(userMenuInput == 2){
                System.out.println("Test image data loaded.");
                byteArray = ConsoleGfx.testImage;
            }// Reads in a hex string with delimiters in RLE format
            else if(userMenuInput == 3){
                System.out.print("Enter an RLE string to be decoded: ");
                String hexString = scanner.next();
                System.out.println();
                byteArray = stringToRle(hexString);
            }// Reads in a hex string without delimiters
            else if(userMenuInput == 4){
                System.out.print("Enter the hex string holding RLE data: ");
                String hexString = scanner.next();
                System.out.println();
                byteArray = stringToData(hexString);
            }// Reads in flat hex string (Not in RLE format)
            else if(userMenuInput == 5){
                System.out.print("Enter the hex string holding flat data: ");
                String hexString = scanner.next();
                System.out.println();
                byteArray = encodeRle(stringToData(hexString));
            }//Displays the loaded image
            else if(userMenuInput == 6){
                System.out.println("Displaying image...");
                if (byteArray.length == 0){
                    System.out.println("(no data)");
                }
                else {
                    ConsoleGfx.displayImage(byteArray);
                }
            }// Displays the RLE representation of the data WITH delimiters
            else if(userMenuInput == 7){
                System.out.print("RLE representation: ");
                if (byteArray.length == 0){
                    System.out.println("(no data)");
                }
                else {
                    System.out.println(toRleString(byteArray));
                }
            }// Displays the RLE representation of the data WITHOUT delimiters
            else if(userMenuInput == 8){
                System.out.print("RLE hex values: ");
                if (byteArray.length == 0){
                    System.out.println("(no data)");
                }
                else {
                    System.out.println(toHexString(byteArray));
                }
            }// Displays the flat hex data used
            else if(userMenuInput == 9) {
                System.out.print("Flat hex values: ");
                if (byteArray.length == 0) {
                    System.out.println("(no data)");
                } else {
                    System.out.println(toHexString(decodeRle(byteArray)));
                }
            }// Makes loop exit properly
            else if(userMenuInput == 0){
            }
            else {
                System.out.println("Error! Invalid Input");
            }
        }
    }// Converts a byte array into a hex string without delimiters - data can be either RLE or raw
    public static String toHexString(byte[] data){
        StringBuilder returnString = new StringBuilder(data.length);
        int placeHolderInt;
        for (byte datum : data) {
            placeHolderInt = datum;
            returnString.append(Integer.toHexString(placeHolderInt));
        }
        return returnString.toString();

    }// Returns the number of "runs" in a data set- how many times you see a number with a different number after it
    public static int countRuns(byte[] flatData){
        int encoding = 999;
        int counter =  0;
        int currentRunLength = 0;
        for(int i=0;i<flatData.length;i++){
            currentRunLength++;
            if(flatData[i] != encoding){
                encoding = flatData[i];
                counter++;
                currentRunLength = 0;
                if(i == flatData.length-1){
                    counter++;
                }
            }// Handles the case where run length is over 15
            else if(currentRunLength == 15){
                counter++;
                currentRunLength = 0;
            }
        }
        return counter;
    }// Takes flat data and returns it in RLE format
    public static byte[] encodeRle(byte[] flatData){
        byte[] returnArray = new byte[2*countRuns(flatData)];
        int encoding = flatData[0];
        int counter = 0;
        int index = 0;
        for(int i=0;i<flatData.length;i++){
            if(flatData[i] != encoding){
                returnArray[index] = (byte) counter;
                returnArray[index+1] = (byte) encoding;
                encoding = flatData[i];
                counter = 1;
                index = index + 2;
                if(i == flatData.length-1){
                    returnArray[index] = (byte) counter;
                    returnArray[index+1] = (byte) encoding;
                }
            }// Handles the case where run length is over 15
            else if(counter == 15){
                returnArray[index] = (byte) counter;
                returnArray[index+1] = (byte) encoding;
                encoding = flatData[i];
                counter = 1;
                index = index + 2;
            }
            else if(i == flatData.length-1){
                counter++;
                returnArray[index] = (byte) counter;
                returnArray[index+1] = (byte) encoding;
            }
            else{
                counter++;
            }
        }

        return returnArray;
    }// Returns the size of the array if you were to decode the rleData passed to it
    public static int getDecodedLength(byte[] rleData){
        int length = 0;
        for(int i=0;i<rleData.length;i=i+2){
            length = length + rleData[i];
        }
        return length;
    }// Turns rle data into flat data, allows for the program to use it properly
    public static byte[] decodeRle(byte[] rleData){
        byte[] returnArray = new byte[getDecodedLength(rleData)];
        int index = 0;
        int counter = rleData[index];
        byte dataum = rleData[index+1];

        for(int i=0;i<returnArray.length;i++){
            if(counter == 0){
                index = index + 2;
                counter = rleData[index];
                dataum = rleData[index+1];
            }
            counter--;
            returnArray[i] = dataum;
        }
        return returnArray;
    }// Turns a hex string to byte array - string can be RLE or flat
    public static byte[] stringToData(String dataString){
        byte[] returnArray = new byte[dataString.length()];
        for(int i = 0;i<dataString.length();i++){
            returnArray[i] = (byte) Character.digit(dataString.charAt(i),16);
        }
        return returnArray;
    }// Takes RLE data and displays it in a more readable format, with delimiters between runs
    public static String toRleString(byte[] rleData){
        StringBuilder str = new StringBuilder(rleData.length);
        for(int i =0;i<rleData.length;i=i+2){
            if (rleData[i] != 0) {
                if(i+2 == rleData.length){
                    str.append(rleData[i]);
                    str.append(Integer.toHexString(rleData[i+1]));
                }
                else{
                    str.append(rleData[i]);
                    str.append(Integer.toHexString(rleData[i+1]));
                    if (rleData[i+2] != 0){
                        str.append(":");
                    }
                }
            }
        }
        return str.toString();
    }// Takes a human readable RLE string with delimiters and turns it into RLE byte data
    public static byte[] stringToRle(String rleString){
        String[] outputStrings = rleString.split(":");
        byte[] returnArray = new byte[2*outputStrings.length];
        for(int i=0;i<2*outputStrings.length;i=i+2){
            if(outputStrings[i/2].length() == 3){
                returnArray[i] = (byte) Integer.parseInt(outputStrings[i/2].substring(0,2));
                returnArray[i+1] = (byte) Integer.parseInt(outputStrings[i/2].substring(2),16);
            }
            else {
                returnArray[i] = (byte) Integer.parseInt(outputStrings[i/2].substring(0,1));
                returnArray[i+1] = (byte) Integer.parseInt(outputStrings[i/2].substring(1),16);
            }
        }
        return returnArray;
    }
}
