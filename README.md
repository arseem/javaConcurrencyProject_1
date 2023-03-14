# **Java concurrency project 1: Thread vs Executor comparision**
Application comparing performance of multithreaded tasks using executorService and manual Thread objects creation.<br>

## About the project
This is an academic assignment for multithreaded programming course. Its main premise was to compare execution times for large number of tasks performed by manually created Thread objects and by executorService. It does so by calculating all the prime numbers in a given range divided into n subsets, where n is a number of tasks to be performed. Those subsets are then computed by individual threads or executor's thread pool.<br><br>
Project contains a simple swing GUI with modifiable key variables and progress bars for each method of implementing multithreading. I also implemented a function that performs a sweep through different values of variables, and saves execution times for both methods to csv files (time per range, time per number of tasks and time per max number of executor threads). Gathered data clearly shows the advantage of executorService while computing large numbers of tasks.<br>
<br>
<p align='center'>
    <img src=./images/example.png ></img>
</p>