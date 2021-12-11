package utils

abstract class AbstractTest {
    Deque<AbstractBlock> blockchain
    int difficulty = 5

    abstract void launchTest()

    abstract boolean isChainValid()

    void addBlock(AbstractBlock newBloc) {
        newBloc.mineBlock(difficulty)
        blockchain.add(newBloc)
    }
}
