# FunnyGuilds Cleaned (#3.9)

- **Tag:** 3.9
- **Published:** 2015-01-07T20:49:44Z
- **GitHub release:** https://github.com/FunnyGuilds/FunnyGuilds/releases/tag/3.9

---

<b>Download</b>: <a href="http://www.dzikoysk.net/projects/funnyguilds/cleaned/3.9/FunnyGuilds.jar">FunnyGuilds Cleaned (#3.9)</a>
<b>Download #</b>:<a href="http://www.dzikoysk.net/projects/funnyguilds/cleaned/3.9/#/FunnyGuilds.jar">FunnyGuilds Cleaned (#3.9)</a> - Pobierz, gdyby pierwszy build wyrzucał z serwera po kliknięciu na ender crystal

<b>Info</b>: Oczywiście jutro rano wejdą pierwsze poprawki, dziś macie jeszcze czas na zasypanie mnie jak największą liczbą nowych błędów :))
- <b>Dodano</b>
  - Komendę <i>'/funnyguillds save-all'</i> pozwalająca zapisać wszystkie dane. Przydatne przy przepisywaniu bazy na pliki flat i odwrotnie.
- <b>Zaktualizowano</b>
  - Wprowadzono nowy system <i>ender crystali</i> - od teraz istnieją one tylko po stronie klienta i nie mają wpływu na pracę serwera. Jest to duży przeskok - pozwalający zwiększyć wydajność, jak i ograniczyć bugi.Dokładniejsze informacje:
    - Usunięte listenery
      - <i>BlockExplode</i>
      - <i>ExplosionPrime</i>
      - <i>ExtendsPiston</i>
    - Inne usprawnione
      - <i>Listener PlayerDamage</i>
      - <i>System ochrony regionu</i>
      - <i>Usunięty task Repeater</i>
    - <b>Uwagi</b>
      - Należy ręcznie pousuwać stare <i>ender crystale</i> (np. komenda <i>'/killall endercrystal'</i> lub po prostu czekać, aż gracze sobie je sami rozwalą :panda_face:)
  - Usunięto eventy
  - Podłączono <i>'player-list-points'</i>
  - Parę zmian w kodzie
    - <i>Zaktualizowano tab</i>
    - </i>Zaktualizowano klasy z refleksjami i wydzielono je do osobnej paczki</i>
  - Inne
- <b>Poprawiono</b>
  - <i>Castowanie</i> obiektów
  - Wyświetlanie punktów pod nickiem
  - Aktualizowanie rankingu <i>(pkt)</i> dla <i>MySQL</i> przy zabiciu gracza
  - Możliwość wyłączenia maksymalnego czasu, przed jakim można przedłużyć gildię
  - Zablokowano podawanie nieprawidłowych graczy w komendzie <i>'/zaproś'</i>

