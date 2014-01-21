(defvar source (make-pathname :name "SSQ" :directory '(:absolute "home" "ethan") :type "TXT"))
(defvar target (make-pathname :name "SSQ-BIN" :directory '(:absolute "home" "ethan") :type "TXT"))

(defun array-to-list (array)
  (let* ((dimensions (array-dimensions array))
         (depth      (1- (length dimensions)))
         (indices    (make-list (1+ depth) :initial-element 0)))
    (labels ((recurse (n)
               (loop for j below (nth n dimensions)
                     do (setf (nth n indices) j)
                     collect (if (= n depth)
                                 (apply #'aref array indices)
                               (recurse (1+ n))))))
      (recurse 0))))


(defun convert-bin()
  (with-open-file(file-in source :direction :input)
    (with-open-file(file-out target :direction :output :if-exists :supersede)
      (read-line file-in nil 'eof)
      (do ((line-in (read-line file-in nil 'eof) (read-line file-in nil 'eof)))
	  ((eql line-in 'eof))
	(let* ((draw (split-sequence:SPLIT-SEQUENCE #\Space line-in))
	       (draw-ar (make-array 50 :initial-element 0)))
	  (setf (elt draw-ar 0) (car draw))
	  (setf (nth 8 draw) (write-to-string (+ (parse-integer (nth 8 draw)) 33)))
	  (mapcar #'(lambda(x) (setf (elt draw-ar (parse-integer x)) 1 )) (cdr (cdr draw)))
	  (format file-out "~{~a ~}~%" (array-to-list draw-ar)))))))


(convert-bin)


