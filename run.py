def main(): 
    import edgar
    edgar.download_index("./data/index", 2015, skip_all_present_except_last=False)    

if __name__ == '__main__':
    main()   
