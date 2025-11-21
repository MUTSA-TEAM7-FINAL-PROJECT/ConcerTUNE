import React, { useState, useEffect } from "react";
import userService from "../services/userService";
import { useNavigate } from "react-router-dom";

const ProfileUpdatePage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    phoneNum: "",
    bio: "",
    tags: "",
  });

  useEffect(() => {
    const load = async () => {
      const data = await userService.getMyProfile();
      setForm({
        username: data.username || "",
        phoneNum: data.phoneNum || "",
        bio: data.bio || "",
        tags: data.tags || "",
      });
    };
    load();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await userService.updateProfile(form);
    navigate("/users/me");
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  return (
    <div className="max-w-xl mx-auto bg-white p-8 shadow rounded-xl mt-8">
      <h1 className="text-3xl font-bold mb-8">프로필 수정</h1>

      <form className="space-y-6" onSubmit={handleSubmit}>
        <div>
          <label className="block font-semibold mb-1">사용자명</label>
          <input
            name="username"
            value={form.username}
            onChange={handleChange}
            className="w-full border px-4 py-2 rounded-lg"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">전화번호</label>
          <input
            name="phoneNum"
            value={form.phoneNum}
            onChange={handleChange}
            className="w-full border px-4 py-2 rounded-lg"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">소개</label>
          <textarea
            name="bio"
            value={form.bio}
            onChange={handleChange}
            className="w-full border px-4 py-2 rounded-lg h-24"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">태그</label>
          <input
            name="tags"
            value={form.tags}
            onChange={handleChange}
            className="w-full border px-4 py-2 rounded-lg"
          />
        </div>

        <button
          type="submit"
          className="w-full bg-indigo-600 text-white py-3 rounded-lg font-semibold hover:bg-indigo-700"
        >
          저장하기
        </button>
      </form>
    </div>
  );
};

export default ProfileUpdatePage;
