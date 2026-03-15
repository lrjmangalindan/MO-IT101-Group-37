import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PayrollSystem {

    static ArrayList<String> empNumbers = new ArrayList<>();
    static ArrayList<String> empNames = new ArrayList<>();
    static ArrayList<String> empBirthdays = new ArrayList<>();
    static ArrayList<Double> empHourlyRates = new ArrayList<>();

    static ArrayList<String> attEmpNums = new ArrayList<>();
    static ArrayList<String> attDates = new ArrayList<>();
    static ArrayList<String> attTimeIns = new ArrayList<>();
    static ArrayList<String> attTimeOuts = new ArrayList<>();

    // {month, startDay, endDay}
    static int[][] cutoffs = {
            { 6, 1, 15 }, { 6, 16, 30 },
            { 7, 1, 15 }, { 7, 16, 31 },
            { 8, 1, 15 }, { 8, 16, 31 },
            { 9, 1, 15 }, { 9, 16, 30 },
            { 10, 1, 15 }, { 10, 16, 31 },
            { 11, 1, 15 }, { 11, 16, 30 },
            { 12, 1, 15 }, { 12, 16, 31 }
    };

    static String[] monthNames = {
            "", "January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December"
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        loadEmployees("../resources/employees.csv");
        loadAttendance("../resources/attendance.csv");

        System.out.print("Enter username: ");
        if (!scanner.hasNextLine()) {
            scanner.close();
            return;
        }
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        if (!scanner.hasNextLine()) {
            scanner.close();
            return;
        }
        String password = scanner.nextLine().trim();

        if (!((username.equals("employee") || username.equals("payroll_staff")) && password.equals("12345"))) {
            System.out.println("Incorrect username and/or password.");
            scanner.close();
            return;
        }

        if (username.equals("employee")) {
            employeeMenu(scanner);
        } else {
            payrollStaffMenu(scanner);
        }

        scanner.close();
    }

    static void loadEmployees(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length < 19)
                    continue;

                String empNum = parts[0].trim();
                String lastName = parts[1].trim().replace("\"", "");
                String firstName = parts[2].trim().replace("\"", "");
                String empName = lastName + ", " + firstName;
                String birthday = parts[3].trim();
                
                String rateStr = parts[18].trim().replace("\"", "").replace(",", "");
                double hourlyRate = 0;
                try {
                    hourlyRate = Double.parseDouble(rateStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                if (!empNumbers.contains(empNum)) {
                    empNumbers.add(empNum);
                    empNames.add(empName);
                    empBirthdays.add(birthday);
                    empHourlyRates.add(hourlyRate);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Employee CSV: " + e.getMessage());
        }
    }

    static void loadAttendance(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length < 6)
                    continue;

                String empNum = parts[0].trim();
                String date = parts[3].trim();
                String timeIn = parts[4].trim();
                String timeOut = parts[5].trim();

                if (empNumbers.contains(empNum)) {
                    attEmpNums.add(empNum);
                    attDates.add(date);
                    attTimeIns.add(timeIn);
                    attTimeOuts.add(timeOut);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Attendance CSV: " + e.getMessage());
        }
    }

    static void employeeMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nDisplay options:");
            System.out.println("1. Enter your employee number");
            System.out.println("2. Exit the program");
            System.out.print("Choose an option: ");
            if (!scanner.hasNextLine())
                return;
            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option.");
                continue;
            }

            if (choice == 1) {
                System.out.print("Enter your employee number: ");
                if (!scanner.hasNextLine())
                    return;
                String empNum = scanner.nextLine().trim();
                int index = empNumbers.indexOf(empNum);
                if (index == -1) {
                    System.out.println("Employee number does not exist.");
                } else {
                    System.out.println("\nEmployee Number: " + empNumbers.get(index));
                    System.out.println("Employee Name: " + empNames.get(index));
                    System.out.println("Birthday: " + empBirthdays.get(index));
                }
            } else if (choice == 2) {
                System.out.println("Exiting the program.");
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    static void payrollStaffMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nDisplay options:");
            System.out.println("1. Process Payroll");
            System.out.println("2. Exit the program");
            System.out.print("Choose an option: ");
            if (!scanner.hasNextLine())
                return;
            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option.");
                continue;
            }

            if (choice == 1) {
                processPayrollMenu(scanner);
            } else if (choice == 2) {
                System.out.println("Exiting the program.");
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    static void processPayrollMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nProcess Payroll (Do not include allowances)");
            System.out.println("Display sub-options:");
            System.out.println("1. One employee");
            System.out.println("2. All employees");
            System.out.println("3. Exit the program");
            System.out.print("Choose an option: ");
            if (!scanner.hasNextLine())
                return;
            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option.");
                continue;
            }

            if (choice == 1) {
                System.out.print("Enter the employee number: ");
                if (!scanner.hasNextLine())
                    return;
                String empNum = scanner.nextLine().trim();
                int index = empNumbers.indexOf(empNum);
                if (index == -1) {
                    System.out.println("Employee number does not exist.");
                } else {
                    displayPayroll(index);
                }
            } else if (choice == 2) {
                for (int i = 0; i < empNumbers.size(); i++) {
                    displayPayroll(i);
                    if (i < empNumbers.size() - 1) {
                        System.out.println("\n========================================");
                    }
                }
            } else if (choice == 3) {
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    static void displayPayroll(int empIndex) {
        String empNum = empNumbers.get(empIndex);
        String empName = empNames.get(empIndex);
        String birthday = empBirthdays.get(empIndex);
        double hourlyRate = empHourlyRates.get(empIndex);

        System.out.println("\nEmployee #: " + empNum);
        System.out.println("Employee Name: " + empName);
        System.out.println("Birthday: " + birthday);

        // (1st and 2nd per month)
        for (int c = 0; c < cutoffs.length; c += 2) {
            int month = cutoffs[c][0];

            double hours1 = calculateHoursForCutoff(empNum, cutoffs[c][0], cutoffs[c][1], cutoffs[c][2]);
            double gross1 = hours1 * hourlyRate;

            double hours2 = calculateHoursForCutoff(empNum, cutoffs[c + 1][0], cutoffs[c + 1][1], cutoffs[c + 1][2]);
            double gross2 = hours2 * hourlyRate;

            double monthlyGross = gross1 + gross2;

            double sss = computeSSS(monthlyGross);
            double philhealth = computePhilHealth(monthlyGross);
            double pagibig = computePagIbig(monthlyGross);
            double tax = computeTax(monthlyGross, sss, philhealth, pagibig);
            double totalDeductions = sss + philhealth + pagibig + tax;

            System.out.println("\nCutoff Date: " + monthNames[month] + " " + cutoffs[c][1]
                    + " to " + monthNames[month] + " " + cutoffs[c][2]);
            System.out.printf("Total Hours Worked: %.2f%n", hours1);
            System.out.printf("Gross Salary: %.2f%n", gross1);
            System.out.printf("Net Salary: %.2f%n", gross1);

            System.out.println("\nCutoff Date: " + monthNames[month] + " " + cutoffs[c + 1][1]
                    + " to " + monthNames[month] + " " + cutoffs[c + 1][2]
                    + " (Second payout includes all deductions)");
            System.out.printf("Total Hours Worked: %.2f%n", hours2);
            System.out.printf("Gross Salary: %.2f%n", gross2);
            System.out.println("Each Deduction:");
            System.out.printf("    SSS: %.2f%n", sss);
            System.out.printf("    PhilHealth: %.2f%n", philhealth);
            System.out.printf("    Pag-IBIG: %.2f%n", pagibig);
            System.out.printf("    Tax: %.2f%n", tax);
            System.out.printf("Total Deductions: %.2f%n", totalDeductions);
            System.out.printf("Net Salary: %.2f%n", gross2 - totalDeductions);
        }
    }

    static double calculateHoursForCutoff(String empNum, int month, int startDay, int endDay) {
        double totalHours = 0;

        for (int i = 0; i < attEmpNums.size(); i++) {
            if (!attEmpNums.get(i).equals(empNum))
                continue;

            String[] dateParts = attDates.get(i).split("/");
            int attMonth = Integer.parseInt(dateParts[0]);
            int attDay = Integer.parseInt(dateParts[1]);

            if (attMonth == month && attDay >= startDay && attDay <= endDay) {
                double hours = calculateHours(attTimeIns.get(i), attTimeOuts.get(i));
                totalHours += hours;
            }
        }

        return totalHours;
    }

    static double calculateHours(String timeIn, String timeOut) {
        int inMinutes = parseTimeToMinutes(timeIn);
        int outMinutes = parseTimeToMinutes(timeOut);

        int effectiveIn = inMinutes;
        if (effectiveIn < 480) {
            effectiveIn = 480;
        } else if (effectiveIn <= 490) {
            effectiveIn = 480;
        }

        int effectiveOut = outMinutes;
        if (effectiveOut > 1020) {
            effectiveOut = 1020;
        }

        double hoursWorked = (effectiveOut - effectiveIn) / 60.0 - 1.0;

        if (hoursWorked < 0)
            hoursWorked = 0;

        return hoursWorked;
    }

    static int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].trim());
        return hours * 60 + minutes;
    }

    static double computeSSS(double monthlyGross) {
        if (monthlyGross < 4250)
            return 4000 * 0.045;
        if (monthlyGross >= 29750)
            return 30000 * 0.045;

        double msc = Math.floor((monthlyGross - 4250) / 500.0) * 500 + 4500;
        if (msc > 30000)
            msc = 30000;
        return msc * 0.045;
    }

    static double computePhilHealth(double monthlyGross) {
        double salary = monthlyGross;
        if (salary < 10000)
            salary = 10000;
        if (salary > 100000)
            salary = 100000;
        return salary * 0.05 / 2.0;
    }

    static double computePagIbig(double monthlyGross) {
        double contribution;
        if (monthlyGross <= 1500) {
            contribution = monthlyGross * 0.01;
        } else {
            contribution = Math.min(monthlyGross, 5000) * 0.02;
        }
        return Math.min(contribution, 100);
    }

    static double computeTax(double monthlyGross, double sss, double philhealth, double pagibig) {
        double taxableIncome = monthlyGross - sss - philhealth - pagibig;
        double tax;

        if (taxableIncome <= 20833) {
            tax = 0;
        } else if (taxableIncome <= 33333) {
            tax = (taxableIncome - 20833) * 0.15;
        } else if (taxableIncome <= 66667) {
            tax = 1875.0 + (taxableIncome - 33333) * 0.20;
        } else if (taxableIncome <= 166667) {
            tax = 8541.80 + (taxableIncome - 66667) * 0.25;
        } else if (taxableIncome <= 666667) {
            tax = 33541.80 + (taxableIncome - 166667) * 0.30;
        } else {
            tax = 183541.80 + (taxableIncome - 666667) * 0.35;
        }

        return tax;
    }
}
