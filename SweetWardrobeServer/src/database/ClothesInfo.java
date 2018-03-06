package database;

import java.util.ArrayList;
import java.util.List;

/*
enum Weather {
    SUNNY, CLOUDY, SHOWERY, RAINY, FOGGY, WINDY, SNOWY
}
*/

public class ClothesInfo {
	public static Integer defaultClothes = -1;
    private List<Suit> suitHistory;         //len = 30  30���ڵĴ�����ʷ
    private List<Weather> weatherHistory;         //len = 30  30���ڵ��������
    private List<Clothes> clothesList;      //�����б�

    ClothesInfo(List<Suit> u_suitHistory, List<Weather> u_weatherHistory, List<Clothes> u_clothesList) {
        suitHistory = u_suitHistory;
        weatherHistory = u_weatherHistory;
        clothesList = u_clothesList;
    }

    ClothesInfo() {
        suitHistory = new ArrayList<>();
        weatherHistory = new ArrayList<>();
        clothesList = new ArrayList<>();
    }

    public void addClothes(Clothes clothes) {
        clothesList.add(clothes);
    }

    public void addWeatherHistory(Weather weather) {
        weatherHistory.add(weather);
    }

    public void addSuitHistory(Suit suit) {
        suitHistory.add(suit);
    }

    public void addHistory(Suit suit, Weather weather) {
        suitHistory.add(suit);
        weatherHistory.add(weather);
    }

    public List<Suit> getSuitHistory() {
        return suitHistory;
    }

    public List<Weather> getWeatherHisory() {
        return weatherHistory;
    }

    public List<Clothes> getClothesList() {
        return clothesList;
    }


}