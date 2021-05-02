# edgar


curl https://www.sec.gov/Archives/edgar/data/320193/000032019321000056/0000320193-21-000056.txt -o test.txt

curl https://www.sec.gov/Archives/edgar/data/1631596/0001140361-21-005284-index.html -o test.html


https://www.sec.gov/os/accessing-edgar-data

pip3 install --user python-edgar
python3 run.py


https://www.sec.gov/Archives/edgar/cik-lookup-data.txt

 In the example above, 0001193125-15-118890 is the accession number, a unique identifier assigned automatically to an accepted submission by EDGAR. The first set of numbers (0001193125) is the CIK of the entity submitting the filing. This could be the company or a third-party filer agent. Some filer agents without a regulatory requirement to make disclosure filings with the SEC have a CIK but no searchable presence in the public EDGAR database. The next two numbers (15) represent the year. The last series of numbers represent a sequential count of submitted filings from that CIK. The count is usually, but not always, reset to zero at the start of each calendar year.