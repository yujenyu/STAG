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
class StagBasicTest {
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
            new StagServer("data/basic-entities.dot", "data/basic-actions.json", runOnPort);
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
                "You are in An empty room.\n" +
                        "You can see:\n" +
                        "Magic potion\n" +
                        "Wooden door\n" +
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
                        response.contains("door") &&
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
        String response = executeCommand("open door");
        log.debug("response: {}", response);
        assertEquals(new ActionRefusedException(playerName).toString(), response);
    }

    @Test
    @Order(4)
    void _4_testUnlock() {
        String response = executeCommand("unlock door with key");
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
        assertEquals("item does not exist in An empty room", response);
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
                        response.contains("door") &&
                        response.contains("forest"));
    }

    @Test
    @Order(10)
    void _10_testGoto() {
        String response = executeCommand("goto forest");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("key") &&
                        response.contains("start"));
    }

    @Test
    @Order(11)
    void _11_testGet() {
        String response = executeCommand("get key");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a key", response);
    }

    @Test
    @Order(12)
    void _12_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, key]"));
    }

    @Test
    @Order(13)
    void _13_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("key") &&
                         response.contains("start"));
    }

    @Test
    @Order(14)
    void _14_testGoto() {
        String response = executeCommand("goto start");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("door") &&
                        response.contains("forest"));
    }

    @Test
    @Order(15)
    void _15_testDrop() {
        String response = executeCommand("drop item");
        log.debug("response: {}", response);
        assertEquals("Specified artefact is not present in your inventory.", response);
    }

    @Test
    @Order(16)
    void _16_testDrop() {
        String response = executeCommand("drop potion");
        log.debug("response: {}", response);
        assertEquals(playerName + " dropped off a potion", response);
    }

    @Test
    @Order(17)
    void _17_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[key]"));
    }

    @Test
    @Order(18)
    void _18_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("potion") &&
                        response.contains("door") &&
                        response.contains("forest"));
    }

    @Test
    @Order(19)
    void _19_testGet() {
        String response = executeCommand("get potion");
        log.debug("response: {}", response);
        assertEquals(playerName + " picked up a potion", response);
    }

    @Test
    @Order(20)
    void _20_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(response.contains("[potion, key]"));
    }

    @Test
    @Order(21)
    void _21_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("potion") &&
                        response.contains("door") &&
                        response.contains("forest"));
    }

    @Test
    @Order(22)
    void _22_openDoor() {
        String response = executeCommand("open door with key");
        log.debug("response: {}", response);
        assertTrue(response.contains("You unlock the door and see steps leading down into a cellar"));
    }

    @Test
    @Order(23)
    void _23_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("door") &&
                        response.contains("forest") &&
                        response.contains("cellar"));
    }

    @Test
    @Order(24)
    void _24_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertTrue(
                !response.contains("key") &&
                          response.contains("potion"));
    }

    @Test
    @Order(25)
    void _25_unlockDoor() {
        String response = executeCommand("unlock door with key");
        log.debug("response: {}", response);
        assertEquals(new ActionRefusedException(playerName).toString(), response);
    }

    @Test
    @Order(26)
    void _26_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertEquals(
                "You are in An empty room.\n" +
                        "You can see:\n" +
                        "Wooden door\n" +
                        "You can access from here:\n" +
                        "forest\n" +
                        "cellar", response);
    }

    @Test
    @Order(27)
    void _27_testGoto() {
        String response = executeCommand("goto cellar");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("Angry Elf") &&
                        response.contains("start"));
    }

    @Test
    @Order(28)
    void _28_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 3 units of health remaining.", response);
    }

    @Test
    @Order(29)
    void _29_testInteract() {
        String response = executeCommand("fight elf");
        log.debug("response: {}", response);
        assertEquals("You attack the elf, but he fights back and you lose some health", response);
    }

    @Test
    @Order(30)
    void _30_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 2 units of health remaining.", response);
    }

    @Test
    @Order(31)
    void _31_testInteract() {
        String response = executeCommand("kick elf");
        log.debug("response: {}", response);
        assertEquals("You attack the elf, but he fights back and you lose some health", response);
    }

    @Test
    @Order(32)
    void _32_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 1 units of health remaining.", response);
    }

    @Test
    @Order(33)
    void _33_testDrink() {
        String response = executeCommand("drink potion");
        log.debug("response: {}", response);
        assertEquals("You drink the potion and your health improves", response);
    }

    @Test
    @Order(34)
    void _34_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertEquals("Your inventory is empty.", response);
    }

    @Test
    @Order(35)
    void _35_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 2 units of health remaining.", response);
    }

    @Test
    @Order(36)
    void _36_testInteract() {
        String response = executeCommand("kill elf");
        log.debug("response: {}", response);
        assertEquals("You attack the elf, but he fights back and you lose some health", response);
    }

    @Test
    @Order(37)
    void _37_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 1 units of health remaining.", response);
    }

    @Test
    @Order(38)
    void _38_testInteract() {
        String response = executeCommand("punch elf");
        log.debug("response: {}", response);
        assertEquals(
                "You attack the elf, but he fights back and you lose some health\n" +
                        "You have died and lose all of your items, return to the start.", response);
    }

    @Test
    @Order(39)
    void _39_testHealth() {
        String response = executeCommand("health");
        log.debug("response: {}", response);
        assertEquals("You have 3 units of health remaining.", response);
    }

    @Test
    @Order(40)
    void _40_testInv() {
        String response = executeCommand("inv");
        log.debug("response: {}", response);
        assertEquals("Your inventory is empty.", response);
    }

    @Test
    @Order(41)
    void _41_testLook() {
        String response = executeCommand("look");
        log.debug("response: {}", response);
        assertEquals("You are in An empty room.\n" +
                "You can see:\n" +
                "Wooden door\n" +
                "You can access from here:\n" +
                "forest\n" +
                "cellar", response);
    }

    @Test
    @Order(42)
    void _42_testGoto() {
        String response = executeCommand("goto cellar");
        log.debug("response: {}", response);
        assertTrue(
                response.contains("Elf") &&
                        response.contains("start"));
    }

    @AfterAll
    static void bye () {
        serverThread.stop();
    }
}