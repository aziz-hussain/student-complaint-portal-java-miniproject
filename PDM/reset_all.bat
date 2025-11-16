@echo off
echo ---------------------------------------------
echo Running SQL reset on EC2
echo ---------------------------------------------

REM ---- SETTINGS ----
set HOST=ec2-3-110-124-58.ap-south-1.compute.amazonaws.com
set USER=superadmin
set PASS=Arwa$1234
set DB=skilo_dev
set SQLFILE=1_create.sql
REM ------------------

echo Using SQL file: %SQLFILE%
echo ---------------------------------------------

mysql -h %HOST% -u %USER% -p%PASS% %DB% < %SQLFILE%

if %errorlevel% equ 0 (
    echo ---------------------------------------------
    echo Done. No errors detected.
    echo ---------------------------------------------
) else (
    echo ---------------------------------------------
    echo Error detected during execution.
    echo ---------------------------------------------
)

pause
