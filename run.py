def main(): 
    import edgar
    edgar.download_index(".", 2020, skip_all_present_except_last=False)    

if __name__ == '__main__':
    main()   
