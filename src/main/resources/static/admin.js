$(document).ready(function () {
    const csrfToken = $('#csrf-token').val();
    let allRoles = [];
    let currentUser = {};

    // Initialize the page
    initPage();

    // Initialize the page with current user data and load users
    function initPage() {
        Promise.all([
            loadAllRoles(),
        ]).then(() => {
            // После загрузки всего загружаем пользователей
            loadCurrentUser();
            loadUsers();
            setupEventHandlers();
        });
    }

    // Load current user info
    function loadCurrentUser() {
        fetch('/api/users/current', {
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => response.json())
            .then(user => {
                currentUser = user;
                displayCurrentUser(user);
            })
            .catch(error => console.error('Error loading current user:', error));
    }

    // Display current user info
    function displayCurrentUser(user) {
        $('#current-user-info').text(`${user.id} # ${user.username}`);

        const rolesHtml = user.roles.map(roleId => {
            const role = allRoles.find(r => r.id === roleId);
            return role ? `<span class="badge bg-primary me-1">${role.name.replace('ROLE_', '')}</span>` : '';
        }).join('');

        $('#current-user-roles').html(rolesHtml);
    }

    // Load all available roles
    function loadAllRoles() {
        return fetch('/api/admin/roles', {
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => response.json())
            .then(roles => {
                allRoles = roles;
                renderRolesForNewUser(roles);
                return roles;
            })
            .catch(error => console.error('Error loading roles:', error));
    }

    // Render roles for new user form
    function renderRolesForNewUser(roles) {
        const rolesHtml = roles.map(role => `
                <div class="form-check">
                    <input class="form-check-input" type="checkbox"
                           id="new-role-${role.id}" value="${role.id}" name="roles">
                    <label class="form-check-label" for="new-role-${role.id}">
                        ${role.name.replace('ROLE_', '')}
                    </label>
                </div>
            `).join('');

        $('#roles-container').html(rolesHtml);
    }

    // Load all users
    function loadUsers() {
        fetch('/api/admin/users', {
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => response.json())
            .then(users => {
                renderUsersTable(users);
                $('#users-count').text(users.length);
            })
            .catch(error => console.error('Error loading users:', error));
    }

    // Render users table
    function renderUsersTable(users) {
        const tableBody = $('#users-table-body');
        tableBody.empty();

        users.forEach(user => {
            const rolesHtml = user.roles.map(roleId => {
                const role = allRoles.find(r => r.id === roleId);
                return role ? `<span class="badge bg-primary me-1">${role.name.replace('ROLE_', '')}</span>` : '';
            }).join('');

            const row = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${rolesHtml}</td>
                        <td class="text-end">
                            <div class="d-flex justify-content-end gap-2">
                                <button class="btn btn-sm btn-primary edit-user" data-user-id="${user.id}">
                                    Edit
                                </button>
                                <button class="btn btn-sm btn-danger delete-user" data-user-id="${user.id}">
                                    Delete
                                </button>
                            </div>
                        </td>
                    </tr>
                `;

            tableBody.append(row);
        });
    }

    // Setup event handlers
    function setupEventHandlers() {
        // New user form submission
        $('#new-user-form').on('submit', function (e) {
            e.preventDefault();
            createNewUser();
        });

        // Edit user form submission
        $('#edit-user-form').on('submit', function (e) {
            e.preventDefault();
            updateUser();
        });

        // Delete user form submission
        $('#delete-user-form').on('submit', function (e) {
            e.preventDefault();

            const userId = $('#delete-user-id').val();

            fetch(`/api/admin/users/${userId}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                }
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(err => Promise.reject(err));
                    }
                    return response;
                })
                .then(() => {
                    // Close the modal
                    $('#deleteUserModal').modal('hide');

                    // Show success message
                    showAlert('User deleted successfully!', 'success');

                    // Reload users list
                    loadUsers();
                })
                .catch(error => {
                    // Show error message
                    const errorMsg = error.message || 'Failed to delete user';
                    showAlert(errorMsg, 'danger');

                    // Optionally keep the modal open to allow retry
                    // $('#deleteUserModal').modal('hide');
                });
        });

        // Logout form submission
        $('#logout-form').on('submit', function (e) {
            e.preventDefault();
            logoutUser();
        });

        // Edit user button click
        $(document).on('click', '.edit-user', function () {
            const userId = $(this).data('user-id');
            openEditUserModal(userId);
        });

        // Delete user button click
        $(document).on('click', '.delete-user', function () {
            const userId = $(this).data('user-id');
            openDeleteUserModal(userId);
        });
    }

    // Create new user
    function createNewUser() {
        const formData = {
            username: $('#newUsername').val(),
            password: $('#newPassword').val(),
            confirmPassword: $('#newConfirmPassword').val(),
            roles: $('#new-user-form input[name="roles"]:checked').map(function () {
                return parseInt(this.value);
            }).get()
        };

        fetch('/api/admin/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => Promise.reject(err));
                }
                return response.json();
            })
            .then(() => {
                $('#new-user-form')[0].reset();
                showAlert('User created successfully!', 'success');
                loadUsers();
                $('.nav-tabs a[href="#users-table"]').tab('show');
            })
            .catch(error => {
                if (error.errors) {
                    displayFormErrors(error.errors);
                } else {
                    showAlert(error.message || 'Failed to create user', 'danger');
                }
            });
    }

    // Open edit user modal
    function openEditUserModal(userId) {
        fetch(`/api/admin/users/${userId}`, {
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => response.json())
            .then(user => {
                $('#edit-user-id').val(user.id);
                $('#edit-username').val(user.username);

                // Render roles for edit form
                const rolesHtml = allRoles.map(role => `
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox"
                                   id="edit-role-${role.id}" value="${role.id}" name="roles"
                                   ${user.roles.includes(role.id) ? 'checked' : ''}>
                            <label class="form-check-label" for="edit-role-${role.id}">
                                ${role.name.replace('ROLE_', '')}
                            </label>
                        </div>
                    `).join('');

                $('#edit-roles-container').html(rolesHtml);

                const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
                modal.show();
            })
            .catch(error => {
                console.error('Error loading user:', error);
                showAlert('Failed to load user data', 'danger');
            });
    }

    // Update user
    function updateUser() {
        const userId = $('#edit-user-id').val();
        const formData = {
            id: parseInt(userId),
            password: $('#edit-password').val(),
            confirmPassword: $('#edit-confirm-password').val(),
            roles: $('#edit-user-form input[name="roles"]:checked').map(function () {
                return parseInt(this.value);
            }).get()
        };

        fetch('/api/admin/users', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => Promise.reject(err));
                }
                return response.json();
            })
            .then(() => {
                $('#editUserModal').modal('hide');
                showAlert('User updated successfully!', 'success');
                loadUsers();

                // If current user updated themselves, reload their info
                if (parseInt(userId) === currentUser.id) {
                    loadCurrentUser();
                }
            })
            .catch(error => {
                if (error.errors) {
                    displayFormErrors(error.errors, 'edit-');
                } else {
                    showAlert(error.message || 'Failed to update user', 'danger');
                }
            });
    }

    // Open delete user modal
    function openDeleteUserModal(userId) {
        fetch(`/api/admin/users/${userId}`, {
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => response.json())
            .then(user => {
                $('#delete-user-id').val(user.id);
                $('#delete-user-id-text').text(user.id);
                $('#delete-username').text(user.username);

                const rolesHtml = user.roles.map(roleId => {
                    const role = allRoles.find(r => r.id === roleId);
                    return role ? `<span class="badge bg-primary me-1">${role.name.replace('ROLE_', '')}</span>` : '';
                }).join('');

                $('#delete-user-roles').html(rolesHtml);

                const modal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
                modal.show();
            })
            .catch(error => {
                console.error('Error loading user:', error);
                showAlert('Failed to load user data', 'danger');
            });
    }

    // Delete user
    function deleteUser() {
        const userId = $('#delete-user-id').val();

        fetch(`/api/admin/users/${userId}`, {
            method: 'DELETE',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => Promise.reject(err));
                }
                return response.json();
            })
            .then(() => {
                $('#deleteUserModal').modal('hide');
                showAlert('User deleted successfully!', 'success');
                loadUsers();

                // If current user deleted themselves, redirect to login
                if (parseInt(userId) === currentUser.id) {
                    window.location.href = '/login';
                }
            })
            .catch(error => {
                showAlert(error.message || 'Failed to delete user', 'danger');
            });
    }

    // Logout user
    function logoutUser() {
        fetch('/logout', {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(() => {
                window.location.href = '/login';
            })
            .catch(error => {
                console.error('Logout failed:', error);
                showAlert('Logout failed', 'danger');
            });
    }

    // Display form errors
    function displayFormErrors(errors, prefix = '') {
        // Reset all error states
        $(`#${prefix}username-error`).text('').parent().removeClass('is-invalid');
        $(`#${prefix}password-error`).text('').parent().removeClass('is-invalid');
        $(`#${prefix}confirmPassword-error`).text('').parent().removeClass('is-invalid');

        // Apply new errors
        Object.keys(errors).forEach(field => {
            const errorMessage = errors[field];
            $(`#${prefix}${field}-error`).text(errorMessage).parent().addClass('is-invalid');
        });
    }

    // Show alert message
    function showAlert(message, type) {
        const alert = $(`
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            `);

        $('.content').prepend(alert);

        setTimeout(() => {
            alert.alert('close');
        }, 5000);
    }

    // Инициализация вкладок
    $('.tabs a').on('click', function(e) {
        e.preventDefault();
        $('.tabs a').removeClass('active');
        $(this).addClass('active');

        const target = $(this).attr('href');
        $('.card').hide();
        $(target).show();
    });

    // По умолчанию показываем таблицу пользователей
    $('.tabs a.active').click();

    // Проверка совпадения паролей при создании пользователя
    $('#new-user form').on('submit', function(e) {
        const password = $('#newPassword').val();
        const confirmPassword = $('#newConfirmPassword').val();

        if (password !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match!');
            return false;
        }
        return true;
    });

    // Проверка совпадения паролей при редактировании пользователя
    $(document).on('submit', '.edit-popover form', function(e) {
        const password = $(this).find('input[name="password"]').val();
        const confirmPassword = $(this).find('input[name="confirmPassword"]').val();

        if (password && password !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match!');
            return false;
        }
        return true;
    });

    // Инициализация popover'ов
    $('[popovertarget]').each(function() {
        const target = $(this).attr('popovertarget');
        $(this).on('click', function() {
            $('#' + target).togglePopover();
        });
    });

    // Закрытие popover'ов при клике вне их области
    $(document).on('click', function(e) {
        if (!$(e.target).closest('[popover], [popovertarget]').length) {
            $('[popover]').hidePopover();
        }
    });

    // Обработка удаления пользователя
    $(document).on('submit', '.delete-popover form', function(e) {
        if (!confirm('Are you sure you want to delete this user?')) {
            e.preventDefault();
            return false;
        }
        return true;
    });
});