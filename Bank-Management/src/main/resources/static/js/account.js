document.addEventListener("DOMContentLoaded", () => {
  const customerId = localStorage.getItem("customerId"); // Or sessionStorage.getItem("customerId")

  if (!customerId) {
    console.error("Customer ID not found in localStorage.");
    return;
  }

  console.log("Loaded customerId:", customerId);

  const mainContent = document.getElementById("main-content");

  const accountDetailsBtn = document.getElementById("menu-account-details");
  const accountBalanceBtn = document.getElementById("menu-account-balance");
  const updateDetailsBtn = document.getElementById("menu-update-details");
  const payeeDetailsBtn = document.getElementById("menu-payee-details");

  // --- Account Details ---
  accountDetailsBtn?.addEventListener("click", async () => {
    try {
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) throw new Error("Failed to fetch account details");
      const accounts = await response.json();

      if (!accounts || accounts.length === 0) {
        mainContent.innerHTML = `<h2>No account data available.</h2>`;
        return;
      }

      const account = accounts[0];
      mainContent.innerHTML = `
        <div class="dashboard-body">
          <h2>Account Details</h2>
          <div class="info-section">
            <div class="info-box">
              <h3>Account Info</h3>
              <p><strong>Account No:</strong> ${account.accountNo}</p>
              <p><strong>Account Type:</strong> ${account.accountType}</p>
              <p><strong>Status:</strong> ${account.status}</p>
              <p><strong>Balance:</strong> ₹${account.balance.toFixed(2)}</p>
              <p><strong>Application Date:</strong> ${account.applicationDate}</p>
            </div>
            <div class="info-box">
              <h3>Customer Info</h3>
              <p><strong>Name:</strong> ${account.fullName}</p>
              <p><strong>Email:</strong> ${account.email}</p>
              <p><strong>Mobile:</strong> ${account.mobileNo}</p>
              <p><strong>Aadhar No:</strong> ${account.aadharNo}</p>
              <p><strong>PAN No:</strong> ${account.panNo}</p>
              <p><strong>Occupation:</strong> ${account.occupation}</p>
              <p><strong>Annual Income:</strong> ₹${account.annualIncome.toLocaleString()}</p>
            </div>
          </div>
        </div>
      `;
    } catch (error) {
      console.error("Error loading account details:", error);
      mainContent.innerHTML = `<h2>Error loading account details. Please try again later.</h2>`;
    }
  });


  // --- Account Balance ---
  accountBalanceBtn?.addEventListener("click", async () => {
    try {
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) throw new Error("Failed to fetch account data");
      const accounts = await response.json();

      if (!accounts || accounts.length === 0) {
        mainContent.innerHTML = "<h2>No account data found.</h2>";
        return;
      }

      const account = accounts[0];
      mainContent.innerHTML = `
        <div class="dashboard-body">
          <h2>Account Balance</h2>
          <div class="balance-info">
            <p><strong>Account No:</strong> ${account.accountNo}</p>
            <p><strong>Balance:</strong> ₹${account.balance.toFixed(2)}</p>
          </div>
          <canvas id="balanceChart" width="400" height="200"></canvas>
        </div>
      `;

      const ctx = document.getElementById('balanceChart').getContext('2d');
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Available Balance'],
          datasets: [{
            label: '₹ Balance',
            data: [account.balance],
            backgroundColor: '#8D1B3D',
            borderColor: '#8D1B3D',
            borderWidth: 1,
            borderRadius: 8
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                callback: value => '₹' + value.toLocaleString()
              }
            }
          }
        }
      });
    } catch (error) {
      console.error("Error loading balance:", error);
      mainContent.innerHTML = "<h2>Error loading balance.</h2>";
    }
  });

  /* --- Update Details ---
  updateDetailsBtn?.addEventListener("click", async () => {
    try {
      const response = await fetch(`/api/accounts/customer/${customerId}`);
      if (!response.ok) throw new Error("Failed to fetch customer details");
      const accounts = await response.json();
      const account = accounts[0];

      mainContent.innerHTML = `
        <div class="dashboard-body">
          <h2>Update Contact Details</h2>
          <form id="update-form" class="update-form">
            <div>
              <label for="email">Email</label>
              <input type="email" id="email" name="email" value="${account.email}" required />
            </div>
            <div>
              <label for="mobileNo">Mobile</label>
              <input type="text" id="mobileNo" name="mobileNo" value="${account.mobileNo}" required />
            </div>
            <button type="submit" class="update-button">Update</button>
          </form>
          <div id="update-status"></div>
        </div>
      `;

      document.getElementById("update-form").addEventListener("submit", async (e) => {
        e.preventDefault();
        const updatedEmail = e.target.email.value;
        const updatedMobile = e.target.mobileNo.value;

        try {
          const updateResponse = await fetch(`/api/accounts/customers/${account.customerId}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: updatedEmail, mobileNo: updatedMobile })
          });

          if (!updateResponse.ok) throw new Error("Failed to update");
          document.getElementById("update-status").innerHTML = "<p style='color:green'>Details updated successfully!</p>";
        } catch (err) {
          console.error("Update error:", err);
          document.getElementById("update-status").innerHTML = "<p style='color:red'>Failed to update. Try again later.</p>";
        }
      });
    } catch (error) {
      console.error("Error loading update form:", error);
      mainContent.innerHTML = "<h2>Error loading update form.</h2>";
    }
  });*/

  // --- Payee Details (View/Add/Delete) ---
  payeeDetailsBtn?.addEventListener("click", () => {
    fetchPayees();
  });

  function fetchPayees() {
    fetch(`http://localhost:80802/api/payees/customer/${customerId}`)
      .then(response => {
        if (!response.ok) throw new Error("Failed to fetch payee data.");
        return response.json();
      })
      .then(payees => {
        if (!Array.isArray(payees) || payees.length === 0) {
          mainContent.innerHTML = `
            <div class="dashboard-body">
              <h2>Payee Details</h2>
              <p style="color: gray;">No payees found.</p>
              <button id="addPayeeBtn" class="btn btn-success">Add Payee</button>
            </div>`;
          document.getElementById("addPayeeBtn").addEventListener("click", addPayeePrompt);
          return;
        }

        const rows = payees.map(p => `
          <tr>
            <td>${p.payeeName}</td>
            <td>${p.bankName}</td>
            <td>${p.payeeAccountNumber}</td>
            <td>${p.ifscCode}</td>
            <td><button class="btn btn-sm btn-danger" onclick="deletePayee(${p.id})">Delete</button></td>
          </tr>`).join("");

        mainContent.innerHTML = `
          <div class="dashboard-body">
            <h2>Payee Details</h2>
            <button id="addPayeeBtn" class="btn btn-success mb-2">Add Payee</button>
            <table class="styled-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Bank</th>
                  <th>Account Number</th>
                  <th>IFSC Code</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>${rows}</tbody>
            </table>
          </div>`;

        document.getElementById("addPayeeBtn").addEventListener("click", addPayeePrompt);
      })
      .catch(error => {
        console.error("Fetch error:", error);
        mainContent.innerHTML = `<p style="color:red;">Failed to load payee data.</p>`;
      });
  }

  window.deletePayee = function(id) {
    if (!confirm("Are you sure you want to delete this payee?")) return;

    fetch(`http://localhost:8082/api/payees/${id}`, {
      method: "DELETE"
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to delete payee.");
        fetchPayees();
      })
      .catch(err => alert(err.message));
  };

  function addPayeePrompt() {
    const name = prompt("Enter payee name:");
    const accNo = prompt("Enter account number:");
    const bank = prompt("Enter bank name:");
    const ifsc = prompt("Enter IFSC code:");

    if (!name || !accNo || !bank || !ifsc) {
      alert("All fields are required.");
      return;
    }

    const newPayee = {
      customerId: customerId,
      payeeName: name,
      payeeAccountNumber: accNo,
      bankName: bank,
      ifscCode: ifsc
    };

    fetch(`http://localhost:8082/api/add`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newPayee)
    })
      .then(response => {
        if (!response.ok) throw new Error("Failed to add payee.");
        fetchPayees();
      })
      .catch(err => alert(err.message));
  }

  // --- Contact Us ---
  document.getElementById("menu-contact-us")?.addEventListener("click", () => {
    mainContent.innerHTML = `
      <div class="dashboard-body">
        <h2>Contact Us</h2>
        <p><strong>Email:</strong> support@QBank.com</p>
        <p><strong>Phone:</strong> 1800-123-4567</p>
        <p><strong>Address:</strong> 101 QBank Towers, Pune, India</p>
      </div>
    `;
  });

  // --- Date/Time Display ---
  const datetimeElem = document.getElementById("datetime");
  if (datetimeElem) {
    const now = new Date();
    datetimeElem.textContent = now.toLocaleString();
  }
});

function logout() {
  if (confirm("Are you sure you want to logout?")) {
    window.location.href = "http://localhost:8080/homepage.html";
  }
}
