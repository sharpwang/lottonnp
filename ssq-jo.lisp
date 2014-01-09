(defvar source (make-pathname :name "SSQ" :directory '(:absolute "home" "ethan") :type "TXT"))
(defvar target (make-pathname :name "SSQ-JO" :directory '(:absolute "home" "ethan") :type "TXT"))

(defun pseudo-cat (file)
  (with-open-file (str file :direction :input)
    (do ((line (read-line str nil 'eof)
               (read-line str nil 'eof)))
        ((eql line 'eof))
      (format t "~A~%" line))))
(defun conver-jo ()
  (with-open-file(file-in source :direction :input)
    (with-open-file(file-out target :direction :output)
      (do ((line-in (read-line file-in nil 'eof)
		    (read-line file-in nil 'eof)))
	  ((eql line-in 'eof))
	(let (draw (split-sequence(line-in 



(pseudo-cat path)
(conver-jo)