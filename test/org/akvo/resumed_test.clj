(ns org.akvo.resumed-test
  (:require [clojure.test :refer :all]
            [org.akvo.resumed :refer :all]
            [ring.mock.request :as m]
            [clojure.java.io :as io])
  (:import java.io.File))

(defn file-to-ba
  "Reads a file to a byte array
  Attribution: http://stackoverflow.com/a/26791567"
  [path]
  (let [f (io/file path)
        ba (byte-array (.length f))
        is (io/input-stream f)]
    (.read is ba)
    (.close is)
    ba))

(deftest test-utilities
  (testing "Testing utility functions"
    (is (= "one.pdf" (get-filename "filename b25lLnBkZg==,meta2 ZHVtbXk=")))
    (is (= true (nil? (get-filename ""))))
    (is (= true (nil? (get-filename nil))))
    (is (= "world_domination_plan.pdf" (get-filename "filename d29ybGRfZG9taW5hdGlvbl9wbGFuLnBkZg==")))
    (is (= "max-age=0" (get-header {:params {} :headers {"tus-resumable" "1.0.0"
                                                         "connection" "keep-alive"
                                                         "cache-control" "max-age=0"}} "Cache-Control")))
    (is (= "https://mysecure-host.org/files" (get-location (m/request :get "https://mysecure-host.org/files"))))
    (is (= "http://localhost:3000/files" (get-location (m/request :get "http://localhost:3000/files"))))
    (is (= "https://some-secure-server/files" (get-location (m/request :get "https://some-secure-server:443/files"))))
    (is (= "http://localhost/files" (get-location (m/request :get "http://localhost:80/files"))))))

(deftest test-options
  (let [handler (make-handler)
        req (m/request :options "http://localhost:3000/files")
        resp (handler req)]
    (testing "OPTIONS"
      (is (= (:status resp) 204))
      (is (= 3 (count (keys (:headers resp)))))
      (is (= (nil? (get-in resp [:headers "Tus-Resumable"])))))))

(deftest good-post
  (let [handler (make-handler)
        um "filename d29ybGRfZG9taW5hdGlvbl9wbGFuLnBkZg==,meta2 ZHVtbXk="
        req (-> (m/request :post "http://localhost:3000/files")
                (m/header "Upload-Metadata" um)
                (m/header "Upload-Length" 10))
        resp (handler req)
        location (get-in resp [:headers "Location"])
        metadata (get-in resp [:headers "Upload-Metadata"])
        tmpdir (str (System/getProperty "java.io.tmpdir") "/resumed")
        upload-id (last (.split location "/" -1))]
    (testing "POST"
      (is (= 201 (:status resp)))
      (is (= true (.startsWith location "http://localhost:3000/files/")))
      (is (= true (.exists (File. (str tmpdir "/" upload-id)))))
      (is (= 0 (.length (File. (str tmpdir "/" upload-id "/world_domination_plan.pdf")))))
      (is (= um metadata)))))