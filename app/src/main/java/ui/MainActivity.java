package ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.barberme.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MyBarberShopsFragment.MyBarberShopsListener {

    final String TAG = "MainActivity";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener firebaseListener;
    Boolean isGuest = false;
    ImageView home;
    ImageView search;
    boolean searchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.menu.search_menu);
        getSupportActionBar().setElevation(0);
        View actionBar = getSupportActionBar().getCustomView();
        home = actionBar.findViewById(R.id.home_button);
        search = actionBar.findViewById(R.id.search_button);
        home.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        search.setOnClickListener(view -> {
            AllBarberShopsFragment myFragment = (AllBarberShopsFragment)getSupportFragmentManager().findFragmentByTag(TAG);
            searchMode = !searchMode;
            myFragment.showHideSearch(searchMode);
        });
        View headerView = navigationView.getHeaderView(0);
        TextView welcomeTV = headerView.findViewById(R.id.navigation_header_tv);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String name;
        if(currentUser.getDisplayName() == null || currentUser.getDisplayName().length() == 0) {
            name = "guest";
            isGuest = true;
        }
        else {
            name = currentUser.getDisplayName();
            isGuest = false;
        }
        welcomeTV.setText("Welcome "+ name);
        welcomeTV.setMovementMethod(LinkMovementMethod.getInstance());
        getSupportFragmentManager().beginTransaction().add(R.id.container, new AllBarberShopsFragment(), TAG).commit();

        firebaseListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(MainActivity.this, SignInUpActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        unCheckAllMenuItems(navigationView.getMenu());
        item.setChecked(true);
        drawerLayout.closeDrawers();
        switch (item.getItemId())
        {
            case R.id.all_barbershops:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new AllBarberShopsFragment(), TAG).addToBackStack(null).commit();
                search.setVisibility(View.VISIBLE);
                searchMode = false;
                break;
            case R.id.my_barbershops:
                if(isGuest != true) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyBarberShopsFragment(), TAG).addToBackStack(null).commit();
                    search.setVisibility(View.GONE);
                }
                else
                    Toast.makeText(this, "You must login to have your our barber shops", Toast.LENGTH_LONG).show();
                break;
            case R.id.Logout:
                logout();
                break;
        }
        return false;
    }

    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Do you want to logout?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(("Yes"), (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                })
                .setNegativeButton(("No"), null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(firebaseListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseListener != null)
            firebaseAuth.removeAuthStateListener(firebaseListener);
    }

    @Override
    public void onAddBarberShopClick() {
        Intent intent = new Intent(this, AddBarberShopActivity.class);
        startActivity(intent);
        finish();
    }
}