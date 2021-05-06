# edgar - mutual fund portfolios

- imports mutual fund monthly portfolio reports from SEC edgar system
- visualizedownloaded reports via goldly
- calculate some statistics

usrs the following libraries:
- tech.ml dataset (for csv file parsing)
- goldly (for visualization)
- datahike (as a database)

```
lein edgar index     ; to build a database
lein edgar info      ; short info on data in database
lein edgar goldly    ; to inspect with browser on port 8000
```

