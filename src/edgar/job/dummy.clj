(ns edgar.job.dummy
   (:require
   [edgar.db :as db]))


(defn add-dummy-data []
  (println "adding dummy reports..")
  (db/add-report {:cik 100334
             :advisor "American Century Mutual Funds, Inc."
             :fund " uuuuu Equity Fund"
             :sid "S000004198"
             :no "xx458"
             :date-filed "2019-10-31"
             :date-report "2019-10-31"})

(db/add-report {:cik 100334
             :advisor "American Century Mutual Funds, Inc."
             :fund " iiiii Equity Fund"
             :sid "S000006198"
             :no "xx499"
             :date-filed "2019-10-31"
             :date-report "2019-10-31"})

(db/add-report {:cik 100334
             :advisor "American Century Mutual Funds, Inc."
             :fund " iiiii Equity Fund"
             :sid "S000006198"
             :no "x7499"
             :date-filed "2018-10-31"
             :date-report "2018-10-31"})

)

(comment

 (db/connect!) 

(db/report-no "xx458")
(db/report-no "xx499")
(db/report-no "x7499")
(db/report-existing? "xx499")
(db/report-existing? "xx458")
(db/all-reports)
(db/reports-for-fund "S000006198")
(db/reports-for-fund "S000004198")
(db/reports-for-fund-by-dbid 13)
(db/reports-for-fund-by-dbid 16)  
(db/fund-bydbid 13)

  ; fund
(db/fund "S000006198")
(db/fund "S000004198")
(db/fund-list)
(db/fund-list-count)

  ; manager 
(db/manager 100334)
(db/funds-of-manager 100334)
(db/manager-list)


  

;
)  
