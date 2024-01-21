import { test, expect } from '@playwright/test';

let apiJWT =
	'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3ZWJjdWJlIiwic3VybmFtZSI6Im1leW5pZWwiLCJmaXJzdG5hbWUiOiJhcnRodXIiLCJyb2xlcyI6W3siaWRSb2xlIjozLCJyb2xlIjoiUk9MRV9SQVRUUkFQQUdFIn1dLCJ1bmlxdWVfbmFtZSI6ImFydGh1ci5tZXluaWVsQHJlc2VhdS5lc2VvLmZyIiwiaXNzIjoiRVNFTyIsImlhdCI6MTcwNTc1NzI4OSwiZXhwIjoxNzA1ODQzNjg5fQ.CNUsQd5JoLrFDKnzcuLUb4Akobr0nqsqSbFfg1x1oz3Of30A4MUMBMJBXHpgaKNNAruBxkRuH--FCf8Vc4cgLw';

test('Generate UML', async ({ page }) => {
    await page.goto('http://localhost:5173/');
    await page.context().addCookies([
        {
            name: 'apiJWT',
            value: apiJWT,
            url: 'http://localhost:5173'
        },
        {
            name: 'azureJWT',
            value:
                'eyJ0eXAiOiJKV1QiLCJub25jZSI6InVZWEdmZHlwdzJsaDBIUkVZdmVZMm1QRG1CQ2JURGRyWVhPWGNnYkZoS28iLCJhbGciOiJSUzI1NiIsIng1dCI6IlQxU3QtZExUdnlXUmd4Ql82NzZ1OGtyWFMtSSIsImtpZCI6IlQxU3QtZExUdnlXUmd4Ql82NzZ1OGtyWFMtSSJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDAiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC80ZDdhZDE1OS0xMjY1LTQzN2EtYjlmNi0yOTQ2MjQ3ZDViZjkvIiwiaWF0IjoxNzAzMDA0NjU3LCJuYmYiOjE3MDMwMDQ2NTcsImV4cCI6MTcwMzAwOTA3MywiYWNjdCI6MCwiYWNyIjoiMSIsImFpbyI6IkFUUUF5LzhWQUFBQWVISGZVN2ZEcWhhQnFGV2dBNkF5cnhTV2xZdUJibWVteHNiWXJ1dUdjUXozWlBqQ0xqZ3B3Qy9lbUdVaUlwckoiLCJhbXIiOlsicHdkIl0sImFwcF9kaXNwbGF5bmFtZSI6IldlYkN1YmUiLCJhcHBpZCI6IjgxOGRhN2Q0LThjYmMtNDBmOS04MDU0LWQwM2ZmNGNmYmQxNyIsImFwcGlkYWNyIjoiMCIsImZhbWlseV9uYW1lIjoiTUVZTklFTCIsImdpdmVuX25hbWUiOiJBcnRodXIiLCJpZHR5cCI6InVzZXIiLCJpcGFkZHIiOiIyYTAxOmNiMDU6ODBjYjpmNjAwOjJjN2Y6ZDQ2YjoxMjM2OmExZmMiLCJuYW1lIjoiTUVZTklFTCBBcnRodXIiLCJvaWQiOiI4ZDUwMTI3Yy00ZmQzLTQzZDMtYWNkNi01MzFmMzg0NWUyZmEiLCJvbnByZW1fc2lkIjoiUy0xLTUtMjEtMTg1NjU2Njg2MC0xNDg2MTQwNDkzLTEyNzMxNjc1ODAtMjcxNjgiLCJwbGF0ZiI6IjUiLCJwdWlkIjoiMTAwMzIwMDA3MEUwMzkwMSIsInJoIjoiMC5BUVVBV2RGNlRXVVNla081OWlsR0pIMWItUU1BQUFBQUFBQUF3QUFBQUFBQUFBQWJBVWMuIiwic2NwIjoiVXNlci5SZWFkIHByb2ZpbGUgb3BlbmlkIGVtYWlsIiwic2lnbmluX3N0YXRlIjpbImttc2kiXSwic3ViIjoia19MdTJnZWx6d0VGeXV5bTZ3cmJZc3JockY0b0dJdkR0NEt0RWxnMU41TSIsInRlbmFudF9yZWdpb25fc2NvcGUiOiJFVSIsInRpZCI6IjRkN2FkMTU5LTEyNjUtNDM3YS1iOWY2LTI5NDYyNDdkNWJmOSIsInVuaXF1ZV9uYW1lIjoiYXJ0aHVyLm1leW5pZWxAcmVzZWF1LmVzZW8uZnIiLCJ1cG4iOiJhcnRodXIubWV5bmllbEByZXNlYXUuZXNlby5mciIsInV0aSI6Ii02cGJnRlpnOFUtRmtLQmVqWDVDQUEiLCJ2ZXIiOiIxLjAiLCJ3aWRzIjpbImI3OWZiZjRkLTNlZjktNDY4OS04MTQzLTc2YjE5NGU4NTUwOSJdLCJ4bXNfc3QiOnsic3ViIjoiWFhIc0xlOHZoc2dmRGRyZnMtNlZoWVRNX0gydXByNDZ6QndVX3RRR00zMCJ9LCJ4bXNfdGNkdCI6MTM2NjE1OTY0NSwieG1zX3RkYnIiOiJFVSJ9.noDGHpqefjNizz9AFXK2vJU-HNTdRYbESSpbG4mJIBm6g3nrMDLpQ7NxJPw7do2n91hdGrGNl75yhZtXkaW7XBGsVUa-awFFYcBLfNO7A8b3hUbbUF8QYlcPETEQTvMq0YuqP_DGApFMb5ljBe41mWUQUzxB8W0fCmVis9lWpcsTPWb83SlyPtWha80otG-LqXp329mphucs5uJpcjdJXIysB3WDEU4kJQUh3dVnQ7URp5TfYO-EuqJw0vf5TShuOoAa7EJYF6E6IxlgGtm4FPr9J5p1xmPPWK7cCoco0kOaQr5g6Ypcc7trPCJmIRA5emexOEEFNL65EhtlCfAOdA',
            url: 'http://localhost:5173'
        }
    ]);

    // Open the TP
	await page.getByRole('button', { name: 'TP-TEST' }).click();

    await page.waitForTimeout(1000);

    await page.locator('#Capa_1 path').click();

    await page.waitForTimeout(1000);

    await expect(page.locator('#mermaid')).toBeVisible();
});


test('Fail generate UML', async ({ page }) => {
    await page.goto('http://localhost:5173/');
    await page.context().addCookies([
        {
            name: 'apiJWT',
            value: apiJWT,
            url: 'http://localhost:5173'
        },
        {
            name: 'azureJWT',
            value:
                'eyJ0eXAiOiJKV1QiLCJub25jZSI6InVZWEdmZHlwdzJsaDBIUkVZdmVZMm1QRG1CQ2JURGRyWVhPWGNnYkZoS28iLCJhbGciOiJSUzI1NiIsIng1dCI6IlQxU3QtZExUdnlXUmd4Ql82NzZ1OGtyWFMtSSIsImtpZCI6IlQxU3QtZExUdnlXUmd4Ql82NzZ1OGtyWFMtSSJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDAiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC80ZDdhZDE1OS0xMjY1LTQzN2EtYjlmNi0yOTQ2MjQ3ZDViZjkvIiwiaWF0IjoxNzAzMDA0NjU3LCJuYmYiOjE3MDMwMDQ2NTcsImV4cCI6MTcwMzAwOTA3MywiYWNjdCI6MCwiYWNyIjoiMSIsImFpbyI6IkFUUUF5LzhWQUFBQWVISGZVN2ZEcWhhQnFGV2dBNkF5cnhTV2xZdUJibWVteHNiWXJ1dUdjUXozWlBqQ0xqZ3B3Qy9lbUdVaUlwckoiLCJhbXIiOlsicHdkIl0sImFwcF9kaXNwbGF5bmFtZSI6IldlYkN1YmUiLCJhcHBpZCI6IjgxOGRhN2Q0LThjYmMtNDBmOS04MDU0LWQwM2ZmNGNmYmQxNyIsImFwcGlkYWNyIjoiMCIsImZhbWlseV9uYW1lIjoiTUVZTklFTCIsImdpdmVuX25hbWUiOiJBcnRodXIiLCJpZHR5cCI6InVzZXIiLCJpcGFkZHIiOiIyYTAxOmNiMDU6ODBjYjpmNjAwOjJjN2Y6ZDQ2YjoxMjM2OmExZmMiLCJuYW1lIjoiTUVZTklFTCBBcnRodXIiLCJvaWQiOiI4ZDUwMTI3Yy00ZmQzLTQzZDMtYWNkNi01MzFmMzg0NWUyZmEiLCJvbnByZW1fc2lkIjoiUy0xLTUtMjEtMTg1NjU2Njg2MC0xNDg2MTQwNDkzLTEyNzMxNjc1ODAtMjcxNjgiLCJwbGF0ZiI6IjUiLCJwdWlkIjoiMTAwMzIwMDA3MEUwMzkwMSIsInJoIjoiMC5BUVVBV2RGNlRXVVNla081OWlsR0pIMWItUU1BQUFBQUFBQUF3QUFBQUFBQUFBQWJBVWMuIiwic2NwIjoiVXNlci5SZWFkIHByb2ZpbGUgb3BlbmlkIGVtYWlsIiwic2lnbmluX3N0YXRlIjpbImttc2kiXSwic3ViIjoia19MdTJnZWx6d0VGeXV5bTZ3cmJZc3JockY0b0dJdkR0NEt0RWxnMU41TSIsInRlbmFudF9yZWdpb25fc2NvcGUiOiJFVSIsInRpZCI6IjRkN2FkMTU5LTEyNjUtNDM3YS1iOWY2LTI5NDYyNDdkNWJmOSIsInVuaXF1ZV9uYW1lIjoiYXJ0aHVyLm1leW5pZWxAcmVzZWF1LmVzZW8uZnIiLCJ1cG4iOiJhcnRodXIubWV5bmllbEByZXNlYXUuZXNlby5mciIsInV0aSI6Ii02cGJnRlpnOFUtRmtLQmVqWDVDQUEiLCJ2ZXIiOiIxLjAiLCJ3aWRzIjpbImI3OWZiZjRkLTNlZjktNDY4OS04MTQzLTc2YjE5NGU4NTUwOSJdLCJ4bXNfc3QiOnsic3ViIjoiWFhIc0xlOHZoc2dmRGRyZnMtNlZoWVRNX0gydXByNDZ6QndVX3RRR00zMCJ9LCJ4bXNfdGNkdCI6MTM2NjE1OTY0NSwieG1zX3RkYnIiOiJFVSJ9.noDGHpqefjNizz9AFXK2vJU-HNTdRYbESSpbG4mJIBm6g3nrMDLpQ7NxJPw7do2n91hdGrGNl75yhZtXkaW7XBGsVUa-awFFYcBLfNO7A8b3hUbbUF8QYlcPETEQTvMq0YuqP_DGApFMb5ljBe41mWUQUzxB8W0fCmVis9lWpcsTPWb83SlyPtWha80otG-LqXp329mphucs5uJpcjdJXIysB3WDEU4kJQUh3dVnQ7URp5TfYO-EuqJw0vf5TShuOoAa7EJYF6E6IxlgGtm4FPr9J5p1xmPPWK7cCoco0kOaQr5g6Ypcc7trPCJmIRA5emexOEEFNL65EhtlCfAOdA',
            url: 'http://localhost:5173'
        }
    ]);


    // TODO: fix this, can't understand why it doesn't work
    await page.locator('#Capa_1 path').click();

    await expect(page.getByLabel('Vous devez d\'abord sélectionner une archive')).toBeVisible();
});

test('Generate UML and modify it', async ({ page }) => {
    await page.goto('http://localhost:5173/');
    await page.context().addCookies([
        {
            name: 'apiJWT',
            value: apiJWT,
            url: 'http://localhost:5173'
        },
        {
            name: 'azureJWT',
            value:
                'eyJ0eXAiOiJKV1QiLCJub25jZSI6InVZWEdmZHlwdzJsaDBIUkVZdmVZMm1QRG1CQ2JURGRyWVhPWGNnYkZoS28iLCJhbGciOiJSUzI1NiIsIng1dCI6IlQxU3QtZExUdnlXUmd4Ql82NzZ1OGtyWFMtSSIsImtpZCI6IlQxU3QtZExUdnlXUmd4Ql82NzZ1OGtyWFMtSSJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDAiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC80ZDdhZDE1OS0xMjY1LTQzN2EtYjlmNi0yOTQ2MjQ3ZDViZjkvIiwiaWF0IjoxNzAzMDA0NjU3LCJuYmYiOjE3MDMwMDQ2NTcsImV4cCI6MTcwMzAwOTA3MywiYWNjdCI6MCwiYWNyIjoiMSIsImFpbyI6IkFUUUF5LzhWQUFBQWVISGZVN2ZEcWhhQnFGV2dBNkF5cnhTV2xZdUJibWVteHNiWXJ1dUdjUXozWlBqQ0xqZ3B3Qy9lbUdVaUlwckoiLCJhbXIiOlsicHdkIl0sImFwcF9kaXNwbGF5bmFtZSI6IldlYkN1YmUiLCJhcHBpZCI6IjgxOGRhN2Q0LThjYmMtNDBmOS04MDU0LWQwM2ZmNGNmYmQxNyIsImFwcGlkYWNyIjoiMCIsImZhbWlseV9uYW1lIjoiTUVZTklFTCIsImdpdmVuX25hbWUiOiJBcnRodXIiLCJpZHR5cCI6InVzZXIiLCJpcGFkZHIiOiIyYTAxOmNiMDU6ODBjYjpmNjAwOjJjN2Y6ZDQ2YjoxMjM2OmExZmMiLCJuYW1lIjoiTUVZTklFTCBBcnRodXIiLCJvaWQiOiI4ZDUwMTI3Yy00ZmQzLTQzZDMtYWNkNi01MzFmMzg0NWUyZmEiLCJvbnByZW1fc2lkIjoiUy0xLTUtMjEtMTg1NjU2Njg2MC0xNDg2MTQwNDkzLTEyNzMxNjc1ODAtMjcxNjgiLCJwbGF0ZiI6IjUiLCJwdWlkIjoiMTAwMzIwMDA3MEUwMzkwMSIsInJoIjoiMC5BUVVBV2RGNlRXVVNla081OWlsR0pIMWItUU1BQUFBQUFBQUF3QUFBQUFBQUFBQWJBVWMuIiwic2NwIjoiVXNlci5SZWFkIHByb2ZpbGUgb3BlbmlkIGVtYWlsIiwic2lnbmluX3N0YXRlIjpbImttc2kiXSwic3ViIjoia19MdTJnZWx6d0VGeXV5bTZ3cmJZc3JockY0b0dJdkR0NEt0RWxnMU41TSIsInRlbmFudF9yZWdpb25fc2NvcGUiOiJFVSIsInRpZCI6IjRkN2FkMTU5LTEyNjUtNDM3YS1iOWY2LTI5NDYyNDdkNWJmOSIsInVuaXF1ZV9uYW1lIjoiYXJ0aHVyLm1leW5pZWxAcmVzZWF1LmVzZW8uZnIiLCJ1cG4iOiJhcnRodXIubWV5bmllbEByZXNlYXUuZXNlby5mciIsInV0aSI6Ii02cGJnRlpnOFUtRmtLQmVqWDVDQUEiLCJ2ZXIiOiIxLjAiLCJ3aWRzIjpbImI3OWZiZjRkLTNlZjktNDY4OS04MTQzLTc2YjE5NGU4NTUwOSJdLCJ4bXNfc3QiOnsic3ViIjoiWFhIc0xlOHZoc2dmRGRyZnMtNlZoWVRNX0gydXByNDZ6QndVX3RRR00zMCJ9LCJ4bXNfdGNkdCI6MTM2NjE1OTY0NSwieG1zX3RkYnIiOiJFVSJ9.noDGHpqefjNizz9AFXK2vJU-HNTdRYbESSpbG4mJIBm6g3nrMDLpQ7NxJPw7do2n91hdGrGNl75yhZtXkaW7XBGsVUa-awFFYcBLfNO7A8b3hUbbUF8QYlcPETEQTvMq0YuqP_DGApFMb5ljBe41mWUQUzxB8W0fCmVis9lWpcsTPWb83SlyPtWha80otG-LqXp329mphucs5uJpcjdJXIysB3WDEU4kJQUh3dVnQ7URp5TfYO-EuqJw0vf5TShuOoAa7EJYF6E6IxlgGtm4FPr9J5p1xmPPWK7cCoco0kOaQr5g6Ypcc7trPCJmIRA5emexOEEFNL65EhtlCfAOdA',
            url: 'http://localhost:5173'
        }
    ]);

    // Open the TP
	await page.getByRole('button', { name: 'TP-TEST' }).click();

    await page.waitForTimeout(1000);

    await page.locator('#Capa_1 path').click();

    await page.locator('label').filter({ hasText: 'HelloWorld' }).click();
    await page.getByText('Main', { exact: true }).first().click();
    await page.locator('label').filter({ hasText: 'MainTest' }).click();

    await expect(page.getByText('No entities to display. Please select some entities.')).toBeVisible();
});