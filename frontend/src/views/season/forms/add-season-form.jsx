/* eslint-disable react/prop-types */
import React, { useState, useEffect } from "react";
import { Form, Modal, Select, message } from "antd";
import { getStudyPrograms } from "@/api/studyProgram";
import { getTahunAjaran } from "@/api/tahun-ajaran";
import { getSemester } from "@/api/semester";
import { getKelas } from "@/api/kelas";
import { getStudents } from "@/api/student";
import { getLectures } from "@/api/lecture";
import { getSubjects } from "@/api/subject";

const { Option } = Select;

const AddSeasonForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();
  const [studyProgramList, setStudyProgramList] = useState([]);
  const [tahunList, setTahunList] = useState([]);
  const [semesterList, setSemesterList] = useState([]);
  const [kelasList, setKelasList] = useState([]);
  const [siswaList, setSiswaList] = useState([]);
  const [guruList, setGuruList] = useState([]);
  const [subjectList, setSubjectList] = useState([]);

  const fetchStudyProgramList = async () => {
    try {
      const result = await getStudyPrograms();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setStudyProgramList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data program studi");
    }
  };

  const fetchTahunAjaranList = async () => {
    try {
      const result = await getTahunAjaran();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setTahunList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data tahun ajaran");
    }
  };

  const fetchSemesterList = async () => {
    try {
      const result = await getSemester();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setSemesterList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data semester");
    }
  };

  const fetchKelasList = async () => {
    try {
      const result = await getKelas();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setKelasList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data kelas");
    }
  };

  const fetchSiswaList = async () => {
    try {
      const result = await getStudents();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setSiswaList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data mahasiswa");
    }
  };

  const fetchSubjectList = async () => {
    try {
      const result = await getSubjects();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setSubjectList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data mata kuliah");
    }
  };

  const fetchGuruList = async () => {
    try {
      const result = await getLectures();
      const { content, statusCode } = result.data;
      if (statusCode === 200) {
        setGuruList(content || []);
      }
    } catch (error) {
      message.error("Gagal memuat data dosen");
    }
  };

  useEffect(() => {
    fetchStudyProgramList();
    fetchTahunAjaranList();
    fetchSemesterList();
    fetchKelasList();
    fetchSiswaList();
    fetchSubjectList();
    fetchGuruList();
  }, []);

  return (
    <Modal
      title="Tambah Kelas Ajaran"
      open={visible}
      onCancel={onCancel}
      onOk={async () => {
        const values = await form.validateFields();
        onOk(values);
      }}
      confirmLoading={confirmLoading}
      width={720}
    >
      <Form form={form} layout="vertical" name="AddSeasonForm">
        <Form.Item
          label="Program Studi"
          name="studyProgram_id"
          rules={[{ required: true, message: "Silakan pilih program studi" }]}
        >
          <Select placeholder="Pilih Program Studi">
            {studyProgramList.map((item) => (
              <Option key={item.id} value={item.id}>
                {item.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Tahun Ajaran"
          name="tahunAjaran_id"
          rules={[{ required: true, message: "Silakan pilih tahun ajaran" }]}
        >
          <Select placeholder="Pilih Tahun Ajaran">
            {tahunList.map((tahun) => (
              <Option key={tahun.idTahun} value={tahun.idTahun}>
                {tahun.tahunAjaran}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Semester"
          name="semester_id"
          rules={[{ required: true, message: "Silakan pilih semester" }]}
        >
          <Select placeholder="Pilih Semester">
            {semesterList.map((semester) => (
              <Option key={semester.idSemester} value={semester.idSemester}>
                {semester.namaSemester}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Kelas"
          name="kelas_id"
          rules={[{ required: true, message: "Silakan pilih kelas" }]}
        >
          <Select placeholder="Pilih Kelas">
            {kelasList.map((kelas) => (
              <Option key={kelas.idKelas} value={kelas.idKelas}>
                {kelas.namaKelas}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Dosen Pengampu"
          name="lecture_id"
          rules={[{ required: true, message: "Silakan pilih dosen" }]}
        >
          <Select placeholder="Pilih Dosen">
            {guruList.map((guru) => (
              <Option key={guru.id} value={guru.id}>
                {guru.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Mahasiswa"
          name="student_id"
          rules={[{ required: true, message: "Silakan pilih minimal 1 mahasiswa" }]}
        >
          <Select mode="multiple" placeholder="Pilih Mahasiswa">
            {siswaList.map((siswa) => (
              <Option key={siswa.id} value={siswa.id}>
                {(siswa.nim || siswa.nisn || "-") + " - " + (siswa.name || "-")}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Mata Kuliah"
          name="subject_id"
          rules={[{ required: true, message: "Silakan pilih minimal 1 mata kuliah" }]}
        >
          <Select mode="multiple" placeholder="Pilih Mata Kuliah">
            {subjectList.map((subject) => (
              <Option key={subject.id} value={subject.id}>
                {subject.name}
              </Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddSeasonForm;