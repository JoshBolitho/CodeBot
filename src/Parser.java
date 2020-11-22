import java.util.*;
import java.util.regex.*;
enum RELOP {
    gt,
    lt,
    eq
}

enum OP {
    add,
    sub,
    mul,
    div
}

enum CONDOP {
    and,
    or,
    not
}

public class Parser {

    static Pattern NUMPAT = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
    static Pattern OPENPAREN = Pattern.compile("\\(");
    static Pattern CLOSEPAREN = Pattern.compile("\\)");
    static Pattern OPENBRACE = Pattern.compile("\\{");
    static Pattern CLOSEBRACE = Pattern.compile("\\}");


    static Pattern ACTPATTERN = Pattern.compile("move|turnL|turnR|takeFuel|wait|turnAround|shieldOn|shieldOff");
    static Pattern MOVE = Pattern.compile("move");
    static Pattern TURNL = Pattern.compile("turnL");
    static Pattern TURNR = Pattern.compile("turnR");
    static Pattern TAKEFUEL = Pattern.compile("takeFuel");
    static Pattern WAIT = Pattern.compile("wait");

    static Pattern TURNAROUND = Pattern.compile("turnAround");
    static Pattern SHIELDON = Pattern.compile("shieldOn");
    static Pattern SHIELDOFF = Pattern.compile("shieldOff");

    static Pattern SEMICOLON = Pattern.compile(";");

    static Pattern LOOP = Pattern.compile("loop");

    static Pattern IFPATTERN = Pattern.compile("if");
    static Pattern WHILEPATTERN= Pattern.compile("while");


    static Pattern RELOPPAT = Pattern.compile("gt|lt|eq");
    static Pattern GTPAT = Pattern.compile("gt");
    static Pattern LTPAT = Pattern.compile("lt");
    static Pattern EQPAT = Pattern.compile("eq");

    static Pattern COMMA = Pattern.compile(",");

    static Pattern FUELLEFT = Pattern.compile("fuelLeft");
    static Pattern OPPLR = Pattern.compile("oppLR");
    static Pattern OPPFB = Pattern.compile("oppFB");
    static Pattern NUMBARRELS = Pattern.compile("numBarrels");
    static Pattern BARRELLR = Pattern.compile("barrelLR");
    static Pattern BARRELFB = Pattern.compile("barrelFB");
    static Pattern WALLDIST = Pattern.compile("wallDist");

    static Pattern SENPAT = Pattern.compile("fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist");

    static Pattern OPPAT = Pattern.compile("add|sub|mul|div");
    static Pattern ADD = Pattern.compile("add");
    static Pattern SUB = Pattern.compile("sub");
    static Pattern MUL = Pattern.compile("mul");
    static Pattern DIV = Pattern.compile("div");

    static Pattern ELSEPAT = Pattern.compile("else");


    static Pattern CONDPAT = Pattern.compile("and|or|not");
    static Pattern ANDPAT = Pattern.compile("and");
    static Pattern ORPAT = Pattern.compile("or");
    static Pattern NOTPAT = Pattern.compile("not");

    public ProgramNode parseScript(String script){
        ProgramNode program = new ProgramNode();

        Scanner scanner = new Scanner(script);
        scanner.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");



        //Split into rows
        String[] lines = script.split("\n");


        return new ProgramNode();
    }



    public Parser() {}
}
