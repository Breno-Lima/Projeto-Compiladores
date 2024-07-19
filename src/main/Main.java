package main;

import lexical.Scanner;
import lexical.Token;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner("teste.mc");
        Token tk;
        while(true) {
            tk = sc.nextToken();
            if(tk == null) {
                break;
            }
            System.out.println(tk);
        }
        System.out.println("Compilation Successful");

    }

}
