package bridge.controller;

import bridge.BridgeNumberGenerator;
import bridge.BridgeRandomNumberGenerator;
import bridge.domain.Bridge;
import bridge.domain.BridgeMaker;
import bridge.domain.Player;
import bridge.view.InputView;
import bridge.view.OutputView;

import java.util.ArrayList;
import java.util.List;

/**
 * 다리 건너기 게임을 관리하는 클래스
 */
public class BridgeGame {
    public static final int START_STAGE = 1;
    public static final String MOVE_INPUT_VALUE = "UD";
    public static final String MOVEMENT_INPUT_VALUE1 = MOVE_INPUT_VALUE;
    public static final String MOVEMENT_INPUT_VALUE = MOVEMENT_INPUT_VALUE1;
    public static final String UNALBE_BLOCK = "X";
    public static final String UNABLE_BLOCK1 = UNALBE_BLOCK;
    private int stage = START_STAGE;
    private BridgeNumberGenerator bridgeNumberGenerator = new BridgeRandomNumberGenerator();
    private BridgeMaker bridgeMaker = new BridgeMaker(bridgeNumberGenerator);
    private Player player = new Player();
    private Bridge bridge;
    private int lastStage;

    public void run() {
        OutputView.printStartMessege();
        lastStage = InputView.readBridgeSize();
        buildBridge(lastStage);
        boolean success = true;

        while (success) {
            success = move(InputView.readMoving(), stage);
            OutputView.printMap(showResult(player.getRecord(), success));
            if (success && stage > lastStage) {
                break;
            }
            if (!success && InputView.readGameCommand().equals("Q")){
                break;
            }
            if (!success){
                success = true;
                retry();
            }
        }

        OutputView.printResult(showResult(player.getRecord(),success),success, player.getTryNumber());
    }

    private List<List<String>> showResult(List<String> playerRecord, boolean success) {
        List<List<String>> result = new ArrayList<>();
        List<String> downBlock = new ArrayList<>(playerRecord);
        List<String> upBlock = new ArrayList<>(playerRecord);
        if (!success && playerRecord.get(playerRecord.size() - 1).equals("D")) {
            downBlock.set(playerRecord.size() - 1, "X");
        }
        if (!success && playerRecord.get(playerRecord.size() - 1).equals("U")) {
            upBlock.set(playerRecord.size() - 1, "X");
        }
        result.add(upBlock);
        result.add(downBlock);
        return result;
    }

    private void buildBridge(int size) {
        bridge = new Bridge(size);
        bridge.build(bridgeMaker.makeBridge(size));
    }

    public boolean move(String playerMoving, int stage) {
        this.stage = stage + 1;
        player.makeRecord(playerMoving);
        return bridge.isCorrect(stage, playerMoving);
    }

    /**
     * 사용자가 게임을 다시 시도할 때 사용하는 메서드
     * <p>
     * 재시작을 위해 필요한 메서드의 반환 타입(return type), 인자(parameter)는 자유롭게 추가하거나 변경할 수 있다.
     */
    public void retry() {
        player.updateTryNumber();
        player.resetRecord();
        this.stage = START_STAGE;
    }
}
