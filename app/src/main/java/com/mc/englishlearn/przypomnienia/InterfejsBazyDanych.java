package com.mc.englishlearn.przypomnienia;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InterfejsBazyDanych {

    @Query("UPDATE fiszki SET tresc =:wierszTresc, czas =:wierszCzas, data =:wierszData  WHERE id = :wierszId")
    void aktualizuj(int wierszId, String wierszTresc, String wierszCzas, String wierszData);
    @Query("SELECT * FROM fiszki")
    List<Model> odczytWszystkichDanych();
    @Delete
    void usunWszystko(List<Model> listaModel);
    @Delete
    void usun(Model model);
    @Insert(onConflict = REPLACE)
    void wprowadzDane(Model model);

}
