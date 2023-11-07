package lotto.service;

import lotto.domain.*;
import lotto.view.InputView;
import lotto.view.OutputView;

import java.util.ArrayList;
import java.util.List;

public class LottoGame {
    private InputView inputView = new InputView();
    private OutputView outputView = new OutputView();
    private RandomNumber randomNumber = new RandomNumber();
    private WinResult winResult = new WinResult();

    public void game() {
        PurchasePrice purchasePrice = inputPurchasePrice();

        int lottoAmount = purchasePrice.getLottoAmount();
        outputView.printLottoAmount(lottoAmount);

        List<Lotto> lottos = generateUserLottos(lottoAmount);
        WinningLotto winningLotto = inputWinLotto();

        inputBonusNumber(winningLotto);

        countWinRank(lottos, winningLotto);

        printWinResult();
        printProfitRate(purchasePrice);
    }

    private PurchasePrice inputPurchasePrice() {
        outputView.printMoneyInputGuideMessage();
        PurchasePrice purchasePrice = inputView.inputPrice();

        return purchasePrice;
    }

    private List<Lotto> generateUserLottos(int lottoAmount) {
        List<Lotto> lottos = new ArrayList<>();

        while (lottoAmount > 0) {
            List<Integer> randomNumbers = randomNumber.generateRandomNumbers();
            outputView.printUserLottos(randomNumbers);
            lottos.add(new Lotto(randomNumbers));
            lottoAmount--;
        }

        return lottos;
    }

    private WinningLotto inputWinLotto() {
        outputView.printWinLottoNumbersInputGuide();
        Lotto winLotto = inputView.inputWinLotto();

        return new WinningLotto(winLotto);
    }

    private void inputBonusNumber(WinningLotto winningLotto) {
        outputView.printBonusNumbersInputGuide();
        validateBonusLottoNumber(winningLotto);
    }

    private void validateBonusLottoNumber(WinningLotto winningLotto) {
        try {
            winningLotto.setBonusLottoNum(inputView.inputBonusNumber());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            validateBonusLottoNumber(winningLotto);
        }
    }

    private void countWinRank(List<Lotto> lottos, WinningLotto winningLotto) {
        for (Lotto userLotto : lottos) {
            int rankValue = winningLotto.matchSameNumberNum(userLotto);
            changeWinResultByRankValue(winningLotto, userLotto, rankValue);
        }
    }

    private void changeWinResultByRankValue(WinningLotto winningLotto, Lotto userLotto, int rankValue) {
        if (winResult.isOverTwo(rankValue)) {
            rankValue = checkSameNumberCountFiveAndContainBonusNumber(winningLotto, userLotto, rankValue);
            increaseWinResultValueByRankValue(rankValue);
        }
    }

    private int checkSameNumberCountFiveAndContainBonusNumber(WinningLotto winningLotto, Lotto userLotto, int rankValue) {
        if (winResult.isFiveSame(rankValue)) {
            return changeRankValueByBonusNum(winningLotto, userLotto);
        }
        return rankValue;
    }

    private int changeRankValueByBonusNum(WinningLotto winningLotto, Lotto userLotto) {
        int rankValue = 5;

        if (winningLotto.isBonusNumContain(userLotto)) {
            rankValue = 7;
        }

        return rankValue;
    }

    private void increaseWinResultValueByRankValue(int rankValue) {
        winResult.increaseWinResultValue(rankValue);
    }

    private void printWinResult() {
        outputView.printWinningResultGuideMessage();
        outputView.printWinResult(winResult);
    }

    private void printProfitRate(PurchasePrice purchasePrice) {
        String profitRate = new ProfitRate().calculateProfitRate(purchasePrice, winResult);
        outputView.printProfitRate(profitRate);
    }
}