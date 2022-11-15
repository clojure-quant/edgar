#!/bin/sh

ls -1U data/nport-xml | wc -l







https://github.com/bbatsov/projectile








    http://www.unexpected-vortices.com/clojure/10-minute-emacs-for-clojure.html

(define-key clojure-mode-map (kbd “s-i”) 'cider-eval-last-sexp)
Rather than showing in minibuffer, use control-o y to insert the result directly in the code.

      (define-key clojure-mode-map (kbd “C-o y”)
        (lambda ()
           (interactive)
           (insert “\n;;=>\n”)
           (cider-eval-last-sexp 't)))

			       


				      
