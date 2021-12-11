import coin.CoinTest
import simple.SimpleTest
import utils.AbstractTest

class Main {
    static void main(String[] args) {
        AbstractTest test
        println "Choose a test:\n\tSimple (1)\n\tCoin (2)\n\tQuit (other)"
        String choice = (System.in.newReader().readLine()).trim()

        try {
            if (choice == "1") {
                test = new SimpleTest(5, 1)
            } else if (choice == "2") {
                test = new CoinTest(1)
            } else {
                throw new Exception("Quit ($choice)")
            }
            test.launchTest()
        } catch (Exception e) {
            println e.message
        }

    }
}
