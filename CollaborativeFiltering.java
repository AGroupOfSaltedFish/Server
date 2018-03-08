package DataBase;


import Jama.Matrix;

import java.util.List;

public class CollaborativeFiltering {

    private Double learningRate, lambda;

    private Integer F, U, I;

    private Integer numIterations;

    private Matrix R, R_predict;

    private Matrix P, Q;

    CollaborativeFiltering(Double learningRate, Double lambda, Integer F, Integer numIterations, List<UserInfo> userInfoList) {
        this.learningRate = learningRate;
        this.lambda = lambda;
        this.numIterations = numIterations;
        this.F = F;
        this.U = userInfoList.size();
        this.I = 33;
        this.Q = Matrix.random(U, F);
        this.P = Matrix.random(I, F);
        this.R = new Matrix(U, I);
        for (Integer u = 0; u < userInfoList.size(); u++) {
            for (Clothes clothes : userInfoList.get(u).getClothesInfo().getClothesList()) {
                R.set(u, clothes.getClothesType(), R.get(u, clothes.getClothesType()) + 1);
                R.set(u, clothes.getClothesColor(), R.get(u, clothes.getClothesColor()) + 1);
            }
        }
    }

    CollaborativeFiltering(Double learningRate, Double lambda, Integer F, Integer numIterations) {
        this.learningRate = learningRate;
        this.lambda = lambda;
        this.numIterations = numIterations;
        this.F = F;
        this.U = 8;
        this.I = 13;
        double[][] arr = {{5, 0, 0, 0, 0, -5, 0, 0, 5, 3, 0, 1, 5},
                {0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 4},
                {0, 0, 1, 0, 2, -5, 4, 0, 0, -2, -2, 0, -2},
                {0, 4, 4, 3, 0, 0, -2, 0, -5, 0, 0, 3, 0},
                {0, 5, -5, 0, -5, 0, 4, 3, 0, 0, 4, 0, 0},
                {0, 0, 4, 0, 0, 3, 0, 0, 4, 0, 0, 0, 0},
                {0, -2, 0, 0, 0, 5, 0, 0, 0, 4, 0, 4, -2},
                {0, -2, 0, 0, 0, 5, 0, 5, 0, 4, 0, 0, -2}};
        this.Q = Matrix.random(U, F);
        this.P = Matrix.random(I, F);
        //  this.Q = new Matrix(U, F, 0.1);
        //  this.P = new Matrix(I, F, 0.1);
        this.R = new Matrix(arr);
    }

    public void optimize() {
        for (Integer ni = 0; ni < numIterations; ni++) {
            for (Integer u = 0; u < U; u++) {
                for (Integer i = 0; i < I; i++) {

                    Matrix Qu = Q.getMatrix(u, u, 0, F - 1);
                    Matrix Pi = P.getMatrix(i, i, 0, F - 1);

                    Matrix dQ = Pi.times(R.get(u, i) - (Qu.times(Pi.transpose())).get(0, 0)).minus(Qu.times(lambda));
                    Matrix dP = Qu.times(R.get(u, i) - (Qu.times(Pi.transpose())).get(0, 0)).minus(Pi.times(lambda));
                    assert dQ.getRowDimension() == 1 && dQ.getColumnDimension() == F;
                    assert dP.getRowDimension() == 1 && dP.getColumnDimension() == F;

                    Qu.plusEquals(dQ.times(learningRate));
                    Pi.plusEquals(dP.times(learningRate));

                    Q.setMatrix(u, u, 0, F - 1, Qu);
                    P.setMatrix(i, i, 0, F - 1, Pi);

                }
            }
        }
    }

    private Double euclideanDistance(Integer user1, Integer user2) {

        Matrix user1Matrix = R_predict.getMatrix(user1, user1, 0, I - 1);
        Matrix user2Matrix = R_predict.getMatrix(user2, user2, 0, I - 1);

        assert user1Matrix.getRowDimension() == 1 && user2Matrix.getRowDimension() == 1;
        Matrix dif = user1Matrix.minus(user2Matrix);
        dif.arrayTimesEquals(dif);
        Double dist = 0.0;
        for (Integer i = 0; i < dif.getColumnDimension(); i++) {
            dist += dif.get(0, i);
        }
        dist = Math.sqrt(dist);

        return dist;

    }

    public Integer recommendUser(Integer userId) {

        optimize();
        printResult();
        Integer recommendedUserId = -1;
        Double minDist = 99999.9;
        for (Integer u = 0; u < R_predict.getRowDimension(); u++) {
            if (!u.equals(userId)) {
                Double dist = euclideanDistance(userId, u);
                System.out.printf("用户%d与用户%d的欧几里得距离为%f\n", userId, u, dist);
                if (dist < minDist) {
                    minDist = dist;
                    recommendedUserId = u;
                }
            }
        }

        System.out.printf("向用户%d推荐用户%d\n", userId, recommendedUserId);

        return recommendedUserId;

    }

    public void printResult() {

        Q.print(F, 2);
        P.print(F, 2);

        R_predict = Q.times(P.transpose());
        R_predict.print(I, 2);

    }


    public static void main(String[] args) {
        double learningRate = 0.01, lambda = 0.7;
        Integer F = 5, numIteration = 100;
/*
        Clothes clothes1 = new Clothes(0, 0, 0, 25, 0); //夹克
        Clothes clothes2 = new Clothes(1, 0, 0, 26, 0); //夹克
        Clothes clothes3 = new Clothes(2, 0, 0, 27, 0); //夹克
        Clothes clothes4 = new Clothes(3, 0, 0, 28, 0); //夹克
        List<Clothes> clothesList1 = new ArrayList<>();
        clothesList1.add(clothes1);
        clothesList1.add(clothes2);
        clothesList1.add(clothes3);
        clothesList1.add(clothes4);
        ClothesInfo clothesInfo1 = new ClothesInfo(clothesList1);
        UserInfo userInfo1 = new UserInfo(110, "cjh", "123456", clothesInfo1);

        Clothes clothes5 = new Clothes(4, 0, 0, 29, 0); //夹克
        Clothes clothes6 = new Clothes(5, 0, 0, 30, 0); //夹克
        Clothes clothes7 = new Clothes(6, 0, 0, 31, 0); //夹克
        Clothes clothes8 = new Clothes(7, 0, 1, 28, 0); //
        List<Clothes> clothesList2 = new ArrayList<>();
        clothesList2.add(clothes5);
        clothesList2.add(clothes6);
        clothesList2.add(clothes7);
        clothesList2.add(clothes8);
        ClothesInfo clothesInfo2 = new ClothesInfo(clothesList2);
        UserInfo userInfo2 = new UserInfo(110, "cjh", "123456", clothesInfo2);

        Clothes clothes9 = new Clothes(8, 0, 2, 30, 0); //
        Clothes clothes10 = new Clothes(9, 0, 3, 31, 0); //
        Clothes clothes11 = new Clothes(10, 0, 4, 32, 0); //
        Clothes clothes12 = new Clothes(11, 0, 5, 32, 0); //
        List<Clothes> clothesList3 = new ArrayList<>();
        clothesList3.add(clothes9);
        clothesList3.add(clothes10);
        clothesList3.add(clothes11);
        clothesList3.add(clothes12);
        ClothesInfo clothesInfo3 = new ClothesInfo(clothesList3);
        UserInfo userInfo3 = new UserInfo(110, "cjh", "123456", clothesInfo3);

        List<UserInfo> userInfoList = new ArrayList<>();
        userInfoList.add(userInfo1);
        userInfoList.add(userInfo2);
        userInfoList.add(userInfo3);
*/
        CollaborativeFiltering LFM = new CollaborativeFiltering(learningRate, lambda, F, numIteration);
        LFM.recommendUser(6);
    }

}
