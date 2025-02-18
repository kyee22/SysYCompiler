
#include<stdio.h>
int getchar() {
    char c;
    scanf("%c", &c);
    return (int)c;
}

int getint() {
    int t;
    scanf("%d", &t);
    while (getchar() != '\n');
    return t;
}
/*
 *  Compiler @ School of Computer Science & Engineering, Beihang University
 *  > TEST Case 1
 */

// Note overflow: when a == 2e9, b == 1e9

int min(int a, int b) {
    if (a < b) {
        return a;
    } else {
        return b;
    }
}


int max(int a, int b) {
    if (a > b) {
        return a;
    } else {
        return b;
    }
}

int ceil_div(int a, int b) {
    // return (a + b - 1) / b;
    return (a - 1) / b + 1;
}

int _gcd(int a, int b) {
    if (!b) {
        return a;
    }
    return _gcd(b, a % b);
}


void do_cpl_fa24_e1_i() {
    /**
     * 10 -100 -12 ==> 1
     * 11 -100 -12 ==> 1
     * 12 -100 -12 ==> 1
     * 13 -100 -12 ==> impossible
     */
    int T;
    T = getint();
    int i = 0;

    for (i = 0; i < T; i = i + 1) { int h, up, down;h = getint();up = getint();down = getint();

        if (up >= h || up - down >= h || -down >= h) {
            printf("1\n");
            continue;
        }

        /**
         *  up >= 0, down >= 0
         *  the final step is made by `up`
         *  others are made by `up-down`
         */
        if (up >= 0 && down >= 0) {
            if (up - down <= 0) {
                printf("Impossible\n");
                continue;
            }
            printf("%d\n", ceil_div(max(0, h - up), up - down) + 1);
        }

        /**
         *  up >= 0, down < 0
         *  always climb up
         */

        if (up >= 0 && down < 0) {
            printf("%d\n", ceil_div(h, up - down));
        }

        /**
         *  up < 0, down < 0
         *  always climb down
         */

        if (up < 0 && down >= 0) {
            printf("Impossible\n");
        }

        /**
         *  up < 0, down < 0
         *  the final step is made by `-down`
         *  others are made by `up-down` (combine `-down` on day_i and `up` on day_i+1)
         */

        if (up < 0 && down < 0) {
            if (up <= down) {
                printf("Impossible\n");
                continue;
            }
            printf("%d\n", ceil_div(max(0, h + down), up - down) + 1);
        }
    }
}

void do_cpl_fa24_e1_j() {
    int m, n;
    m = getint();
    n = getint();

    int tot = 0, i, j;

    for (i = 0; i <= m; i = i + 1) {
        for (j = 0; j <= n; j = j + 1) {
            if (i == 0 && j == 0) {
                continue;
            }
            tot = tot + _gcd(i, j) - 1;
        }
    }

    printf("%d\n", tot);
}

void do_cpl_fa24_e1_h() {
    int n;
    n = getint();
    int ans = 1;
    int _max = -1;
    int i;
    for (i = 1; i <= n; i = i + 1) {
        int tmp;
        tmp = getint();
        if (tmp >= _max) {
            _max = tmp;
            ans = i;
        }
    }

    printf("%d\n", ans);
}

int abs(int a) {
    if (a < 0) {
        return -a;
    }
    return a;
}


int validate(int a, int b, int c) {
    if (a + b > c && a + c > b && b + c > a
           && abs(a - b) < c  && abs(a - c) < b && abs(c - b) < a){
        return 1;
    } else {
        return 0;
    }
}

void do_cpl_fa24_e1_b() {
    int price = 259;
    int yuan, jiao, fen;
    yuan = getint();
    jiao = getint();
    fen = getint();
    printf("%d\n", (100 * yuan + 10 * jiao + fen) / price);
}

void do_cpl_fa24_e1_c() {
    int n, ci, target;
    n = getint();

    int sum = 0;
    for (; n; ) {
        n = n - 1;
        ci = getint();
        sum = sum + ci;
    }

    target = getint();
    printf("%d\n", min(target, max(0, sum)));

    if (sum <= 0) {
        printf("Moca finish 0 requirement!\n");
    }
    if (sum >= target) {
        printf("Moca finish all requirements!\n");
    }
}

void do_cpl_fa24_e1_d() {
    int x, y;
    int m, n;
    n = getint();
    m = getint();

    if ((4 * n - m) % 2 != 0) {
        printf("No answer\n");
        return;
    }
    if ((m - 2 * n) % 2 != 0) {
        printf("No answer\n");
        return;
    }

    x = (4 * n - m) / 2;
    y = (m - 2 * n) / 2;

    if (x < 0 || y < 0) {
        printf("No answer\n");
        return;
    }

    printf("%d %d\n", x, y);
}


void do_cpl_fa24_e1_e() {
    int a, b, c;
    a = getint();
    b = getint();
    c = getint();

    int cond = validate(a, b, c);
    if (!validate(a, b, c)) {
        printf("Not triangle\n");
        return;
    }

    if (a * a + b * b == c * c || a * a + c * c == b * b || b * b + c * c == a * a) {
        printf("Right triangle\n");
    } else if (a * a + b * b < c * c || a * a + c * c < b * b || b * b + c * c < a * a) {
        printf("Obtuse triangle\n");
    } else {
        printf("Acute triangle\n");
    }

    if (a == b || b == c || a == c) {
        printf("Isosceles triangle\n");
    }
    if (a == b && b == c) {
        printf("Equilateral triangle\n");
    }
}

void do_cpl_fa24_e1_f() {
    int num;
    num = getint();

    for (; num; ) {
        int digit = num % 10;
        if (digit == 6) {
            printf("Ban lP\n");
            return;
        }
        num = num / 10;
    }

    printf("Xhesica win\n");
    return ;
}

void do_cpl_fa24_e1_g() {
    int a, b, p;
    a = getint();
    b = getint();
    p = getint();

    int _mod = (a + b) % p;

    if (_mod < 0) {
        _mod = _mod + p;
    }
    printf("%d\n", _mod);
}

int isLeapYear(int a)
{
	if (0 == a % 4 && a % 100 != 0
	    || 0 == a % 400)
	{
		return 1;
	}
	else
	{
		int x = 1 + 2;
	}
	return 0;
}

int SimpleCond1(int num) {
    if (num % 3 == 0 || num % 5 == 0) {
        return 1;
    }
    return 0;
}

int SimpleCond2(int num) {
    if (num % 13 == 0 || num % 17 == 0 && num % 19 != 0) {
        return 1;
    }
    return 0;
}

int complexConditionCheck(int num) {
    if (num > 0 && num < 100 && num % 2 == 0 ||
        num >= 100 && num < 200 && SimpleCond1(num) ||
        num >= 200 && num < 300 && num % 7 == 0 && num % 11 != 0 ||
        num >= 300 && num <= 400 &&  SimpleCond2(num) ||
        num > 400 && num % 23 == 0 && num % 29 != 0) {
        return 1; // fffff
    }
    return 0; // ccccc
}



int main() {
    printf("22371092\n");
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_b (1/1)\n");
    do_cpl_fa24_e1_b();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_c (2/3)\n");
    do_cpl_fa24_e1_c();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_c (2/3)\n");
    do_cpl_fa24_e1_c();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_c (3/3)\n");
    do_cpl_fa24_e1_c();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_d (1/2)\n");
    do_cpl_fa24_e1_d();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_d (2/2)\n");
    do_cpl_fa24_e1_d();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_e (1/2)\n");
    do_cpl_fa24_e1_e();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_e (2/2)\n");
    do_cpl_fa24_e1_e();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_f (1/2)\n");
    do_cpl_fa24_e1_f();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_f (2/2)\n");
    do_cpl_fa24_e1_f();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (1/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (2/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (3/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (4/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (5/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (6/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (7/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (8/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (9/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_g (10/10)\n");
    do_cpl_fa24_e1_g();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_h (1/1)\n");
    do_cpl_fa24_e1_h();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_i (1/1)\n");
    do_cpl_fa24_e1_i();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_j (1/3)\n");
    do_cpl_fa24_e1_j();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_j (2/3)\n");
    do_cpl_fa24_e1_j();
    printf("============================================\n");
    printf("> TEST do_cpl_fa24_e1_j (3/3)\n");
    do_cpl_fa24_e1_j();
    printf("===========================\n");
    printf("> TEST isLeapYear:\n");

	int Year = 0;
    Year = 2100;

	if (isLeapYear(Year)) {
	    printf("%d is a leap year!!\n", Year);
	} else {
	    printf("%d is not a leap year!!\n", Year);
	}
    printf("===========================\n");
    printf("> TEST complex_conditions:\n");

    int testNumber = 0;
    for (; testNumber <= 1000; testNumber = testNumber + 1)
        if (complexConditionCheck(testNumber))
            printf("%d meets the complex conditions.\n", testNumber);
        else
            printf("%d does not meet the complex conditions.\n", testNumber);

    return 0;
}
