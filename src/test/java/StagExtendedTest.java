import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import Exception.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StagExtendedTest {
    static private final int runOnPort = 8889;
    static private final String playerName = "test";
    static private Thread serverThread;

    public static String executeCommand(String command) {
        try (
                Socket socket = new Socket("127.0.0.1", runOnPort);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            List<String> response = new ArrayList<>();
            response.add("");
            String incoming;
            out.write(playerName + ": " + command + "\n");
            out.flush();

            while ((incoming = in.readLine()) != null) {
                response.add(incoming);
                response.add("\n");
            }
            log.debug(String.join("", response));
            response.remove(response.size()-1);
            return String.join("", response);
        } catch (IOException ioe) {
            System.out.println(ioe);
            return "";
        }
    }

    @BeforeAll
    static void init() throws IOException, InterruptedException {
        serverThread = new Thread(() -> {
            new StagServer("data/extended-entities.dot", "data/extended-actions.json", runOnPort);
        });
        serverThread.start();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    @Order(0)
    void _0_testFormat() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertEquals(
                "You are in A log cabin in the woods.\n" +
                        "You can see:\n" +
                        "A bottle of magic potion\n" +
                        "A razor sharp axe\n" +
                        "A silver coin\n" +
                        "A locked wooden trapdoor in the floor\n" +
                        "You can access from here:\n" +
                        "forest", response);
    }

    @Test
    @Order(1)
    void _1_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("potion") &&
                        response.contains("axe") &&
                        response.contains("coin") &&
                        response.contains("trapdoor") &&
                        response.contains("forest"));
    }

    @Test
    @Order(2)
    void _2_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertEquals("Your inventory is empty.", response);
    }

    @Test
    @Order(3)
    void _3_testOpen() {
        String response = executeCommand("open trapdoor");
        log.debug("response: {}", response);
        assertEquals(new ActionRefusedException(playerName).toString(), response);
    }

    @Test
    @Order(4)
    void _4_testUnlock() {
        String response = executeCommand("unlock trapdoor with key");
        log.debug("response: {}", response);
        assertEquals(new ActionRefusedException(playerName).toString(), response);
    }

    @Test
    @Order(5)
    void _5_testGet() {
        String response = executeCommand("get");
        log.debug("response: {}", response);
        assertEquals("What artifact do you want to get?", response);
    }

    @Test
    @Order(6)
    void _6_testGet() {
        String response = executeCommand("get item");
        log.debug("response: {}", response);
        assertEquals("item does not exist in A log cabin in the woods", response);
    }

    @Test
    @Order(7)
    void _7_testGet() {
        String response = executeCommand("get potion");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a potion", response);
    }

    @Test
    @Order(8)
    void _8_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion]"));
    }

    @Test
    @Order(9)
    void _9_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("potion") &&
                        response.contains("axe") &&
                        response.contains("coin") &&
                        response.contains("trapdoor") &&
                        response.contains("forest"));
    }

    @Test
    @Order(10)
    void _10_testGet() {
        String response = executeCommand("get axe");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a axe", response);
    }

    @Test
    @Order(11)
    void _11_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, axe]"));
    }

    @Test
    @Order(12)
    void _12_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("potion") &&
                        !response.contains("axe") &&
                        response.contains("coin") &&
                        response.contains("trapdoor") &&
                        response.contains("forest"));
    }

    @Test
    @Order(13)
    void _13_testGet() {
        String response = executeCommand("get coin");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a coin", response);
    }

    @Test
    @Order(14)
    void _14_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, axe, coin]"));
    }

    @Test
    @Order(15)
    void _15_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("potion") &&
                        !response.contains("axe") &&
                        !response.contains("coin") &&
                        response.contains("trapdoor") &&
                        response.contains("forest"));
    }

    @Test
    @Order(16)
    void _16_testGoto() {
        String response = executeCommand("goto forest");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("key") &&
                        response.contains("tree") &&
                        response.contains("cabin") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(17)
    void _17_testGet() {
        String response = executeCommand("get key");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a key", response);
    }

    @Test
    @Order(18)
    void _18_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, key, axe, coin]"));
    }

    @Test
    @Order(19)
    void _19_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("key") &&
                        response.contains("tree") &&
                        response.contains("cabin") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(20)
    void _20_testCut() {
        String response = executeCommand("cutdown tree with axe");
        log.debug("response: {}", response);
        assertEquals("You cut down the tree with the axe", response);
    }

    @Test
    @Order(21)
    void _21_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, key, axe, coin]"));
    }

    @Test
    @Order(22)
    void _22_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("tree") &&
                        response.contains("log") &&
                        response.contains("cabin") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(23)
    void _23_testGet() {
        String response = executeCommand("get log");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a log", response);
    }

    @Test
    @Order(24)
    void _24_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, log, key, axe, coin]"));
    }

    @Test
    @Order(25)
    void _25_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("log") &&
                        response.contains("cabin") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(26)
    void _26_testGoto() {
        String response = executeCommand("goto cabin");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("trapdoor") &&
                        response.contains("forest"));
    }

    @Test
    @Order(27)
    void _27_testUnlock() {
        String response = executeCommand("unlock trapdoor with key");
        log.debug("response: {}", response);
        assertEquals("You unlock the trapdoor and see steps leading down into a cellar", response);
    }

    @Test
    @Order(28)
    void _28_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(!response.contains("key"));
    }

    @Test
    @Order(29)
    void _29_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("trapdoor") &&
                        response.contains("forest") &&
                        response.contains("cellar"));
    }

    @Test
    @Order(30)
    void _30_testOpen() {
        String response = executeCommand("open trapdoor with key");
        log.debug("response: {}", response);
        assertEquals(new ActionRefusedException(playerName).toString(), response);
    }

    @Test
    @Order(31)
    void _31_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertEquals(
                "You are in A log cabin in the woods.\n" +
                        "You can see:\n" +
                        "A locked wooden trapdoor in the floor\n" +
                        "You can access from here:\n" +
                        "forest\n" +
                        "cellar", response);
    }

    @Test
    @Order(32)
    void _32_testGoto() {
        String response = executeCommand("goto cellar");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("Elf") &&
                        response.contains("cabin"));
    }

    @Test
    @Order(33)
    void _33_testPay() {
        String response = executeCommand("pay elf a coin");
        log.debug("response: {}", response);
        assertEquals("You pay the elf your silver coin and he produces a shovel", response);
    }

    @Test
    @Order(34)
    void _34_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(!response.contains("coin"));
    }

    @Test
    @Order(35)
    void _35_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("shovel") &&
                        response.contains("Elf") &&
                        response.contains("cabin"));
    }

    @Test
    @Order(36)
    void _36_testGet() {
        String response = executeCommand("get shovel");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a shovel", response);
    }

    @Test
    @Order(37)
    void _37_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("shovel"));
    }

    @Test
    @Order(38)
    void _38_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("shovel") &&
                        response.contains("Elf") &&
                        response.contains("cabin"));
    }

    @Test
    @Order(39)
    void _39_testGoto() {
        String response = executeCommand("goto cabin");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("trapdoor") &&
                        response.contains("forest") &&
                        response.contains("cellar"));
    }

    @Test
    @Order(40)
    void _40_testGoto() {
        String response = executeCommand("goto forest");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("cabin") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(41)
    void _41_testGoto() {
        String response = executeCommand("goto riverbank");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("horn") &&
                        response.contains("river") &&
                        response.contains("forest"));
    }

    @Test
    @Order(42)
    void _42_testGet() {
        String response = executeCommand("get horn");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a horn", response);
    }

    @Test
    @Order(43)
    void _43_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("horn"));
    }

    @Test
    @Order(44)
    void _44_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("horn") &&
                        response.contains("river") &&
                        response.contains("forest"));
    }

    @Test
    @Order(45)
    void _45_testBridge() {
        String response = executeCommand("bridge river with log");
        log.debug("response: {}", response);
        assertEquals("You bridge the river with the log and can now reach the other side", response);
    }

    @Test
    @Order(46)
    void _46_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(!response.contains("log"));
    }

    @Test
    @Order(47)
    void _47_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("river") &&
                        response.contains("forest") &&
                        response.contains("clearing"));
    }

    @Test
    @Order(48)
    void _48_testGoto() {
        String response = executeCommand("goto clearing");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("It looks like the soil has been recently disturbed") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(49)
    void _49_testDig() {
        String response = executeCommand("dig ground with shovel");
        log.debug("response: {}", response);
        assertEquals("You dig into the soft ground and unearth a pot of gold !!!", response);
    }

    @Test
    @Order(50)
    void _50_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("It looks like the soil has been recently disturbed")  &&
                        response.contains("gold") &&
                        response.contains("hole") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(51)
    void _51_testGet() {
        String response = executeCommand("get gold");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a gold", response);
    }

    @Test
    @Order(52)
    void _52_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("gold"));
    }

    @Test
    @Order(53)
    void _53_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("gold"));
    }

    @Test
    @Order(54)
    void _54_testGoto() {
        String response = executeCommand("goto riverbank");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("river") &&
                        response.contains("forest") &&
                        response.contains("clearing"));
    }

    @Test
    @Order(55)
    void _55_testGoto() {
        String response = executeCommand("goto forest");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("cabin") &&
                        response.contains("riverbank"));
    }

    @Test
    @Order(56)
    void _56_testGoto() {
        String response = executeCommand("goto cabin");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("trapdoor") &&
                        response.contains("forest") &&
                        response.contains("cellar"));
    }

    @Test
    @Order(57)
    void _57_testBlow() {
        String response = executeCommand("blow horn");
        log.debug("response: {}", response);
        assertEquals("You blow the horn and as if by magic, a lumberjack appears !", response);
    }

    @Test
    @Order(58)
    void _58_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(response.contains("A burly wood cutter"));
    }

    @Test
    @Order(59)
    void _59_testGoto() {
        String response = executeCommand("goto cellar");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("Elf") &&
                        response.contains("cabin"));
    }

    @Test
    @Order(60)
    void _60_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 3 units of health remaining.", response);
    }

    @Test
    @Order(61)
    void _61_testInteract() {
        String response = executeCommand("hit elf");
        log.debug("response: {}", response);
        assertEquals("You attack the elf, but he fights back and you lose some health", response);
    }

    @Test
    @Order(62)
    void _62_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 2 units of health remaining.", response);
    }

    @Test
    @Order(63)
    void _63_testInteract() {
        String response = executeCommand("punch elf");
        log.debug("response: {}", response);
        assertEquals("You attack the elf, but he fights back and you lose some health", response);
    }

    @Test
    @Order(64)
    void _64_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 1 units of health remaining.", response);
    }

    @Test
    @Order(65)
    void _65_testDrink() {
        String response = executeCommand("drink potion");
        log.debug("response: {}", response);
        assertEquals("You drink the potion and your health improves", response);
    }

    @Test
    @Order(66)
    void _66_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(!response.contains("potion"));
    }

    @Test
    @Order(67)
    void _67_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 2 units of health remaining.", response);
    }

    @Test
    @Order(68)
    void _68_testInteract() {
        String response = executeCommand("slap elf");
        log.debug("response: {}", response);
        assertEquals("You attack the elf, but he fights back and you lose some health", response);
    }

    @Test
    @Order(69)
    void _69_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 1 units of health remaining.", response);
    }

    @Test
    @Order(70)
    void _70_testInteract() {
        String response = executeCommand("attack elf");
        log.debug("response: {}", response);
        assertEquals(
                "You attack the elf, but he fights back and you lose some health\n" +
                        "You have died and lose all of your items, return to the start.", response);
    }

    @Test
    @Order(71)
    void _71_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 3 units of health remaining.", response);
    }

    @Test
    @Order(72)
    void _72_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertEquals("Your inventory is empty.", response);
    }

    @Test
    @Order(73)
    void _73_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertEquals(
                "You are in A log cabin in the woods.\n" +
                        "You can see:\n" +
                        "A burly wood cutter\n" +
                        "A locked wooden trapdoor in the floor\n" +
                        "You can access from here:\n" +
                        "forest\n" +
                        "cellar", response);
    }

    @Test
    @Order(74)
    void _74_testGoto() {
        String response = executeCommand("goto cellar");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("axe") &&
                        response.contains("horn") &&
                        response.contains("shovel") &&
                        response.contains("gold") &&
                        response.contains("Elf") &&
                        response.contains("cabin"));
    }

    @Test
    @Order(75)
    void _75_testNoSuchComd() {
        String response = executeCommand("gota cellar");
        log.debug("response: {}", response);
        assertEquals("Please ensure you input a valid command.", response);
    }

    @AfterAll
    static void bye () {
        serverThread.stop();
    }
}