package com.mc.englishlearn.przypomnienia;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


@Dao
public interface DAO {

//Zapytania do bazy danych

    @Insert(onConflict = REPLACE)
    void wprowadz(Dane dane);

    @Delete
    void usun(Dane dane);

    @Delete
    void resetowanie(List<Dane> mainData);

    @Query("UPDATE fiszki SET tresc =:wierszTresc, czas =:wierszCzas, data =:wierszData  WHERE id = :wierszId")
    void aktualizuj(int wierszId, String wierszTresc, String wierszCzas, String wierszData);

    @Query("SELECT * FROM fiszki")
    List<Dane> czytajWszystkie();

}
